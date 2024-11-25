package com.bank.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.InvalidAttributesException;
import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.dto.CheckBalanceRequest;
import com.bank.dto.CreateAccountRequest;
import com.bank.dto.CreateAccountResponse;
import com.bank.dto.CreateCustomerWithAccountRequest;
import com.bank.dto.CreateCustomerWithAccountResponse;
import com.bank.dto.DeleteAccountRequest;
import com.bank.dto.DisableRequest;
import com.bank.dto.GetAccountRequest;
import com.bank.factory.AccountRepositoryFactory;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountService {
	
//	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	AccountRepositoryFactory accountRepositoryFactory;
	
//	@Autowired
	CustomerRepository customerRepo;

//	@Autowired
	TransactionRepository transactionRepo;

	@Autowired
	Utility utility;

	Random myRandom = new Random();

	// generating account number
	private long generateAccountNumber() {
		StringBuilder accountNumberString = new StringBuilder();
		for (int i = 1; i <= 12; i++) {
			accountNumberString.append(myRandom.nextInt(8) + 1);
		}
		return Long.parseLong(accountNumberString.toString());
	}

	// generating customer id
	private long generateCustomerId() {
		StringBuilder customerIdString = new StringBuilder();
		for (int i = 1; i <= 8; i++) {
			customerIdString.append(myRandom.nextInt(8) + 1);
		}
		return Long.parseLong(customerIdString.toString());
	}

	// generating pin number
	private int generatePinNumber() {
		StringBuilder pinNumberString = new StringBuilder();
		for (int i = 1; i <= 4; i++) {
			pinNumberString.append(myRandom.nextInt(8) + 1);
		}
		return Integer.parseInt(pinNumberString.toString());
	}

	public CreateCustomerWithAccountResponse createCustomerWithAccount(CreateCustomerWithAccountRequest createRequestObj) throws InstanceAlreadyExistsException {
		long customerId = generateCustomerId();
		long accountNumber = generateAccountNumber();
		int pinNumber = generatePinNumber();
		
		// checking if account with current adhaar number already exist
		List<Customer> customerList = customerRepo.findAll();
		for (Customer obj : customerList) {
			if (createRequestObj.getAdhaarNumber() == obj.getAdhaarNumber()) {
				throw new InstanceAlreadyExistsException("Account with current Adhaar Number already exist");
			}
		}
		
		Customer newCustomer = Customer.builder().customerId(customerId).customerName(createRequestObj.getCustomerName())
				.phoneNumber(createRequestObj.getPhoneNumber()).adhaarNumber(createRequestObj.getAdhaarNumber())
				.email(createRequestObj.getEmail()).dob(createRequestObj.getDob()).address(createRequestObj.getAddress())
				.build();
		customerRepo.save(newCustomer);

		log.debug("Customer Object after saving: {}", newCustomer);
		
		Account newAccount = Account.builder().accountNumber(accountNumber).customerId(customerId)
				.accountType(createRequestObj.getAccountType()).balance(createRequestObj.getBalance())
				.pinNumber(pinNumber).isActive(true).build();
		accountRepo.save(newAccount);
		
		log.debug("Account Object after saving: {}", newAccount);
		
		return new CreateCustomerWithAccountResponse(createRequestObj.getCustomerName(), 
				customerId, accountNumber, pinNumber);
	}
	
	
	public CreateAccountResponse createAccount(CreateAccountRequest accountRequestObj) {
		// Checking for valid customerId
		utility.checkForCustomerIdLength(accountRequestObj.getCustomerId());
		
		long accountNumber = generateAccountNumber();
		int pinNumber = generatePinNumber();
		List<Customer> customerList = customerRepo.findAll();

		for (Customer obj : customerList) {
			if (accountRequestObj.getCustomerId() == obj.getCustomerId()) {
				
				Account accountObj = Account.builder().accountNumber(accountNumber)
						.customerId(accountRequestObj.getCustomerId()).accountType(accountRequestObj.getAccountType())
						.balance(accountRequestObj.getBalance()).pinNumber(pinNumber).isActive(true).build();
				accountRepo.save(accountObj);
				
				log.debug("Account Object after saving: {}", accountObj);
				
				return new CreateAccountResponse(obj.getCustomerName(), accountNumber, obj.getCustomerId());
				}
		}
		throw new IllegalArgumentException("Customer does not exist"); 
	}
	
	
	public List<Account> getList(GetAccountRequest getAccountRequestObj) {
		// Checking for valid customerId
		utility.checkForCustomerIdLength(getAccountRequestObj.getCustomerId());
		
		List<Account> accountList = new ArrayList<>();
		List<Account> allAccounts = accountRepo.findAll();
		
		for (Account obj : allAccounts) {
			if ((getAccountRequestObj.getCustomerId() == obj.getCustomerId()) && (obj.isActive())) {
				accountList.add(obj);
			}
		}
		return accountList;
	}
	
	
	public Map<String, String> balanceCheck(CheckBalanceRequest checkBalanceRequestObj)
			throws InvalidAttributesException, AuthenticationException {
		Account accountObj = accountRepo.findByAccountNumber(checkBalanceRequestObj.getAccountNumber());
		// If account does not exist
		utility.checkForInvalidAccountNumber(accountObj);

		// checking for valid pin number
		utility.checkForInvalidPinNumber(accountObj, checkBalanceRequestObj.getPinNumber());

		// Checking if account is not Active
		utility.checkIfInActive(accountObj);

		HashMap<String, String> response = new HashMap<>();
		response.put("message","Available Balance: " + accountObj.getBalance());
		return response;
	}

	public Map<String, String> disable(DisableRequest disableRequestObj)
			throws InvalidAttributesException, AuthenticationException {
		Account accountObj = accountRepo.findByAccountNumber(disableRequestObj.getAccountNumber());

		// If account does not exist
		utility.checkForInvalidAccountNumber(accountObj);

		// checking for valid pin number
		utility.checkForInvalidPinNumber(accountObj, disableRequestObj.getPinNumber());

		// Checking if account is not Active
		utility.checkIfInActive(accountObj);

		// Log
		log.debug("Account Details Before Removing: {}", accountObj);

		accountObj.setActive(false);
		accountRepo.save(accountObj);

		// Log
		log.debug("Account Details After Removing: {}", accountObj);

		HashMap<String, String> response = new HashMap<>();
		response.put("message", "Success");
		return response;

	}
	
	public Map<String, String> deleteAccount(DeleteAccountRequest deleteAccountRequest) throws InvalidAttributesException {
		Account accountObj = accountRepo.findByAccountNumber(deleteAccountRequest.getAccountNumber());
		// If account does not exist
		utility.checkForInvalidAccountNumber(accountObj);
		
		accountRepo.deleteById(deleteAccountRequest.getAccountNumber());
		
		Map<String, String> response = new HashMap<>();
		response.put("message", "Account Deleted Successfully");
		return response;
	}

}

package com.bank.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.InvalidAttributesException;
import javax.security.sasl.AuthenticationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.CheckBalanceRequest;
import com.bank.dto.CreateAccountRequest;
import com.bank.dto.CreateAccountResponse;
import com.bank.dto.CreateCustomerWithAccountRequest;
import com.bank.dto.CreateCustomerWithAccountResponse;
import com.bank.dto.DeleteAccountRequest;
import com.bank.dto.DisableRequest;
import com.bank.dto.GetAccountRequest;
import com.bank.model.Account;
import com.bank.model.DateOfBirth;
import com.bank.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	@Autowired
	AccountService accountService;
	
	@Autowired
	ControllerUtility controllerUtil;
	
	DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	LocalDateTime now = LocalDateTime.now();
	
	@PostMapping("/create")
	public CreateCustomerWithAccountResponse addCustomerWithAccount(@RequestBody @Valid CreateCustomerWithAccountRequest createCustomerWithAccObj) throws InstanceAlreadyExistsException {
		
		// checking for valid date and month in date-of-birth
		DateOfBirth dateOfBirth = createCustomerWithAccObj.getDob();
		controllerUtil.checkForValidDayAndMonth(dateOfBirth.getDay(), dateOfBirth.getMonth());
		
		// getting current year and checking for valid age and year
		String currentDate = date.format(now);
		controllerUtil.checkForValidAgeAndYear(currentDate, dateOfBirth);
		
		// checking for valid phone number length
		String stringPhoneNumber = String.valueOf(createCustomerWithAccObj.getPhoneNumber());
		controllerUtil.checkForValidPhoneNumber(stringPhoneNumber);

		// checking for valid aadhar number length
		String stringAdhaarNumber = String.valueOf(createCustomerWithAccObj.getAdhaarNumber());
		controllerUtil.checkForValidAdhaarLength(stringAdhaarNumber);

		return accountService.createCustomerWithAccount(createCustomerWithAccObj);

	}
	
	@PostMapping("/add-account")
	public CreateAccountResponse addAccount(@RequestBody CreateAccountRequest accountRequestObj) {
		return accountService.createAccount(accountRequestObj);
	}
	
	@PostMapping("/get-accounts")
	List<Account> listAccount(@RequestBody GetAccountRequest getAccountRequestObj) {
		return accountService.getList(getAccountRequestObj);
	}
	
	@PostMapping("/balance-check")
	public Map<String, String> checkBalance(@RequestBody CheckBalanceRequest checkBalanceRequestObj) throws InvalidAttributesException, AuthenticationException {
		return accountService.balanceCheck(checkBalanceRequestObj);
	}
	
	@PutMapping("/disable")
	public Map<String, String> disableAccount(@RequestBody DisableRequest disableRequestObj) throws AuthenticationException, InvalidAttributesException {
		return accountService.disable(disableRequestObj);
	}
	
	@DeleteMapping()
	public Map<String, String> removeAccount(@RequestBody DeleteAccountRequest deleteAccountRequest) throws InvalidAttributesException {
		return accountService.deleteAccount(deleteAccountRequest);
	}
	
}

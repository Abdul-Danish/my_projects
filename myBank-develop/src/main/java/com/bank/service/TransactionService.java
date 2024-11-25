package com.bank.service;

import java.time.format.DateTimeFormatter;
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
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.DeleteTransactionRequest;
import com.bank.dto.DepositRequest;
import com.bank.dto.MiniStatementRequest;
import com.bank.dto.TransferRequest;
import com.bank.dto.WithdrawRequest;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionService {

	@Autowired
	Utility utility;

//	@Autowired
	AccountRepository accountRepo;

//	@Autowired
	TransactionRepository transactionRepo;

	DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");	// HH:mm:ss
	

	Random myRandom = new Random();
	// Generating Transaction id
	public long transactionIdGenerator() {
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= 8; i++) {
			builder.append(myRandom.nextInt(8) + 1);
		}
		return Long.parseLong(builder.toString());
	}
	
	@Transactional
	public Map<String, String> transferMoney(TransferRequest transferRequestObj)
			throws InvalidAttributesException, AuthenticationException, InstanceAlreadyExistsException {

		// Checking if sender and receiver account numbers are same
		utility.checkForSameAccountNumber(transferRequestObj.getSendersAccountNumber(), transferRequestObj.getReceiversAccountNumber());
		
		// Checking for invalid amount
		utility.checkForInvalidAmount(transferRequestObj.getAmount());

		Account senderAccountObj = accountRepo.findByAccountNumber(transferRequestObj.getSendersAccountNumber());
		// if sender account number is incorrect
		if (senderAccountObj == null) {
			throw new InvalidAttributesException("Invalid Sender's Account Number");
		}

		// checking for valid pin number
		utility.checkForInvalidPinNumber(senderAccountObj, transferRequestObj.getSenderPinNumber());

		// if sender account is disabled
		utility.checkIfInActive(senderAccountObj);

		// checking for sufficient balance
		utility.checkForInsufficientBalance(senderAccountObj, transferRequestObj.getAmount());

		// Log
		log.debug("Sender Details Before Transaction: {}", senderAccountObj);

		Account receiverAccountObj = accountRepo.findByAccountNumber(transferRequestObj.getReceiversAccountNumber());
		// if receiver account number is incorrect
		if (receiverAccountObj == null) {
			throw new InvalidAttributesException("Invalid Receiver's Account Number");
		}
		
		// if receiver account is disabled
		utility.checkIfInActive(receiverAccountObj);

		// Log
		log.debug("Receiver Details Before Transaction: {}", receiverAccountObj);

		long senderAccountBalance = senderAccountObj.getBalance();
		long deduce = senderAccountBalance - transferRequestObj.getAmount();
		senderAccountObj.setBalance(deduce);
		accountRepo.save(senderAccountObj);

		long randomId1 = transactionIdGenerator();
		Transaction senderTransactionObj = Transaction.builder().transactionId(randomId1)
				.accountNumber(senderAccountObj.getAccountNumber()).customerId(senderAccountObj.getCustomerId())
				.amount(transferRequestObj.getAmount()).transactionType(TransactionType.WITHDRAW).build();
		transactionRepo.save(senderTransactionObj);
		
		// Log
		log.debug("Sender Details After Transaction: {}", senderAccountObj);

		// adding into receiver's customer object
		long receiverAccountBalance = receiverAccountObj.getBalance();
		long total = receiverAccountBalance + transferRequestObj.getAmount();
		receiverAccountObj.setBalance(total);
		accountRepo.save(receiverAccountObj);

		// adding into receiver's transaction object
		long randomId2 = transactionIdGenerator();
		Transaction receiverTransactionObj = Transaction.builder().transactionId(randomId2)
				.accountNumber(receiverAccountObj.getAccountNumber()).customerId(receiverAccountObj.getCustomerId())
				.amount(transferRequestObj.getAmount()).transactionType(TransactionType.DEPOSIT).build();
		transactionRepo.save(receiverTransactionObj);
		
		// Log
		log.debug("Receiver Details After Transaction: {}", receiverAccountObj);

		HashMap<String, String> response = new HashMap<>();
		response.put("message","Available Balance: " + receiverAccountObj.getBalance());
		return response;
	}
	
	@Transactional
	public Map<String, String> depositMoney(DepositRequest depositRequestObj) throws InvalidAttributesException {
		
		// Checking for invalid amount
		utility.checkForInvalidAmount(depositRequestObj.getAmount());

		// Checking if account exist
		Account accountObj = accountRepo.findByAccountNumber(depositRequestObj.getAccountNumber());
		utility.checkForInvalidAccountNumber(accountObj);

		// Checking if account is Active
		utility.checkIfInActive(accountObj);

		// Log
		log.debug("Account Details Before Transaction: {}", accountObj);

		long total = accountObj.getBalance() + depositRequestObj.getAmount();
		accountObj.setBalance(total);
		accountRepo.save(accountObj);
		
		long randomId = transactionIdGenerator();
		Transaction transaction = Transaction.builder().transactionId(randomId).accountNumber(accountObj.getAccountNumber())
				.customerId(accountObj.getCustomerId()).amount(depositRequestObj.getAmount())
				.transactionType(TransactionType.DEPOSIT).build();
		transactionRepo.save(transaction);
		
		// Log
		log.debug("Account Details After Transaction: {}", accountObj);

		HashMap<String, String> response = new HashMap<>();
		response.put("message","Available Balance: " + accountObj.getBalance());
		return response;
	}

	@Transactional
	public Map<String, String> withdrawMoney(WithdrawRequest withdrawRequestObj) throws InvalidAttributesException, AuthenticationException {
		
		Account accountObj = accountRepo.findByAccountNumber(withdrawRequestObj.getAccountNumber());
		// checking for invalid amount
		utility.checkForInvalidAmount(withdrawRequestObj.getAmount());
		
		// If account does not exist
		utility.checkForInvalidAccountNumber(accountObj);
		
		// checking for valid pin number
		utility.checkForInvalidPinNumber(accountObj, withdrawRequestObj.getPinNumber());
		
		// Checking if account is Active
		utility.checkIfInActive(accountObj);
		
		// Checking for sufficient balance
		utility.checkForInsufficientBalance(accountObj, withdrawRequestObj.getAmount());
		
		// Log
		log.debug("Account Details Before Transaction: {}", accountObj);
		
		long deduce = accountObj.getBalance() - withdrawRequestObj.getAmount();
		accountObj.setBalance(deduce);
		
		accountRepo.save(accountObj);

		long randomId = transactionIdGenerator();
		Transaction transaction = Transaction.builder().transactionId(randomId).accountNumber(accountObj.getAccountNumber())
				.customerId(accountObj.getCustomerId()).amount(withdrawRequestObj.getAmount())
				.transactionType(TransactionType.WITHDRAW).build();
		transactionRepo.save(transaction);
		
		// Log
		log.debug("Account Details After Transaction: {}", accountObj);
		
		HashMap<String, String> response = new HashMap<>();
		response.put("message","Available Balance: " + accountObj.getBalance());
		return response;
		
	}
		
	
	public List<Transaction> getMiniStatement(MiniStatementRequest miniStatementRequestObj) throws InvalidAttributesException, AuthenticationException {
		
		Account accountObj = accountRepo.findByAccountNumber(miniStatementRequestObj.getAccountNumber());
		// If account does not exist
		utility.checkForInvalidAccountNumber(accountObj);
		
		// checking for valid pin number
		utility.checkForInvalidPinNumber(accountObj, miniStatementRequestObj.getPinNumber());
		
		// Checking if account is not Active
		utility.checkIfInActive(accountObj);
		
		List<Transaction> transactionsList = new ArrayList<>();
		List<Transaction> transactions = transactionRepo.findByAccountNumber(miniStatementRequestObj.getAccountNumber());
		int totalSize = transactions.size();
		
		int i = 1;
		if (totalSize >= 2) {
			while (i <=2 ) {
				transactionsList.add(transactions.get(totalSize-i));
				i ++;
			}
			return transactionsList;
		}
		else if (totalSize == 1) {
			transactionsList.add(transactions.get(0));
			return transactionsList;
		}
		
		return transactionsList;
	}
	
	
	public Map<String, String> deleteTransaction(DeleteTransactionRequest deleteTransactionRequest) {
		transactionRepo.deleteById(deleteTransactionRequest.getTransactionId());
		
		Map<String, String> response = new HashMap<>();
		response.put("message", "Transaction Deleted Successfully");
		return response;
	}
	
	
//	public List<Transaction> getMiniStatement(MiniStatementRequest miniStatementRequestObj) throws AuthenticationException, InvalidAttributesException {
//
//		Account accountObj = accountRepo.findByAccountNumber(miniStatementRequestObj.getAccountNumber());
//		// If account does not exist
//		utility.checkForInvalidAccountNumber(accountObj);
//		
//		// checking for valid pin number
//		utility.checkForInvalidPinNumber(accountObj, miniStatementRequestObj.getPinNumber());
//		
//		// Checking if account is not Active
//		utility.checkIfInActive(accountObj);
//		
//		List<Transaction> transactionsList = new ArrayList<>();
//		List<Transaction> transactions = transactionRepo.findByAccountNumber(miniStatementRequestObj.getAccountNumber());
//		
//		log.debug("transaction list: {}", transactions);
//		System.out.println(transactions);
//		
//		try {
//			
//		// getting last transaction date
//		int length = transactions.size();
//		Transaction lastTransaction = transactions.get(length-1);
//		String lastTransactionDateTime = lastTransaction.getCreatedDate().toString();		// changed
//		String lastTransactionDate = lastTransactionDateTime.split("T")[0];
//		String[] lastTransactionList = lastTransactionDate.split("-");
//		int lastTransactionDay = Integer.parseInt(lastTransactionList[2]);
//		int lastTransactionMonth = Integer.parseInt(lastTransactionList[1]);
//		int lastTransactionYear = Integer.parseInt(lastTransactionList[0]);
//		
//		
//		for (Transaction obj: transactions) {
//			String transactionDateTime = obj.getCreatedDate().toString();		// changed
//			String transactionDate = transactionDateTime.split("T")[0];
//			String[] transactionDateList = transactionDate.split("-");
//			int transactionDay = Integer.parseInt(transactionDateList[2]);
//			int transactionMonth = Integer.parseInt(transactionDateList[1]);
//			int transactionYear = Integer.parseInt(transactionDateList[0]);
//
//			
//			if ((lastTransactionMonth != transactionMonth) || (lastTransactionYear != transactionYear)) {
//				continue;
//			}
//			if ((lastTransactionDay - transactionDay) <= 2) {
//				transactionsList.add(obj);
//			}
//		}
//		
//		} catch(IndexOutOfBoundsException ex) {
//			throw new IndexOutOfBoundsException("No Transactions");
//		}
//		
//		return  transactionsList;
//	}

	
}

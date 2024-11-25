package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.InvalidAttributesException;
import javax.security.sasl.AuthenticationException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bank.dto.DepositRequest;
import com.bank.dto.MiniStatementRequest;
import com.bank.dto.TransferRequest;
import com.bank.dto.WithdrawRequest;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.Address;
import com.bank.model.Customer;
import com.bank.model.DateOfBirth;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.mongo.AccountRepositoryMongo;
import com.bank.repository.mongo.TransactionRepositoryMongo;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceUnitTest {

	@Autowired
	TransactionService transactionService;
	
	@MockBean
	TransactionRepositoryMongo transactionRepo;
	
	@MockBean
	AccountRepositoryMongo accountRepo;
	
	
	DateOfBirth dobObj1 = null;
	DateOfBirth dobObj2 = null;
	
	Address addressObj1 = null;
	Address addressObj2 = null;
	
	Customer customerObj1 = null;
	Customer customerObj2 = null;
	
	Account accountObj1 = null;
	Account accountObj2 = null;
	Account accountObj3 = null;
	
	Transaction transactionObj1 = null;
	Transaction transactionObj2 = null;
	
	
	@BeforeAll
	void setUp() throws ParseException {
		
		dobObj1 = DateOfBirth.builder().day(12).month(11).year(1987).build();
		dobObj2 = DateOfBirth.builder().day(20).month(9).year(1995).build();
		
		addressObj1 = Address.builder().houseNumber("34-5/1").zipCode(30070).city("Mumbai").build();
		addressObj2 = Address.builder().houseNumber("43-8/3").zipCode(40050).city("Delhi").build();
		
		customerObj1 = Customer.builder().customerId(82498224L).customerName("John").phoneNumber(8470293024L)
				.adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj1).address(addressObj1).build();
		customerObj2 = Customer.builder().customerId(73298092L).customerName("Sam").phoneNumber(8247928734L)
				.adhaarNumber(734728370294L).email("sam@gmail.com").dob(dobObj2).address(addressObj2).build();
		
		accountObj1 = Account.builder().accountNumber(798340293802L).customerId(82498224L)
				.accountType(AccountType.SAVINGS).balance(5000).pinNumber(1726).isActive(true).build();
		accountObj2 = Account.builder().accountNumber(322983792321L).customerId(73298092L)
				.accountType(AccountType.SAVINGS).balance(4000).pinNumber(8746).isActive(true).build();
		accountObj3 = Account.builder().accountNumber(732948729709L).customerId(53480392L)
				.accountType(AccountType.CURRENT).balance(2000).pinNumber(4527).isActive(false).build();
		
		
//		LocalDateTime now = LocalDateTime.now();
		
//		String pattern = "yyyy-MMM-dd HH:mm:ss";
//		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);
//		String date1 = "2023-01-11 12:54:51";
//		Date date = formatter.parse(date1);
		
//		LocalDate date2 = LocalDate.parse("2023-01-11 12:54:51.332");

		
		transactionObj1 = Transaction.builder().transactionId(83487938L).accountNumber(798340293802L)
				.customerId(82498224L).amount(500)
				.transactionType(TransactionType.WITHDRAW).build();
		transactionObj2 = Transaction.builder().transactionId(79374209L).accountNumber(322983792321L)
				.customerId(73298092L).amount(100).
				transactionType(TransactionType.WITHDRAW).build(); 
	}
	
	// transferMoney - positive test
	
	@Test
	void transferMoneyTest() throws AuthenticationException, InstanceAlreadyExistsException, InvalidAttributesException {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(798340293802L)
				.receiversAccountNumber(322983792321L).amount(100).senderPinNumber(1726).build();
		
		when(accountRepo.findByAccountNumber(paramObj.getSendersAccountNumber())).thenReturn(accountObj1);
		when(accountRepo.findByAccountNumber(paramObj.getReceiversAccountNumber())).thenReturn(accountObj2);
		when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(i -> i.getArguments()[0]);
		when(transactionRepo.save(Mockito.any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
		
		Map<String, String> response = transactionService.transferMoney(paramObj);
		assertEquals("Available Balance: 4100", response.get("message"));
	}

	// transfer - negative test cases
	
	@Test
	void transferWithSameAccountNumber() {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(798340293802L)
				.receiversAccountNumber(798340293802L).amount(100).senderPinNumber(1726).build();
		
		assertThrows(IllegalArgumentException.class, () -> transactionService.transferMoney(paramObj));
	}
	
	@Test
	void transferWithInvalidAmount() {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(798340293802L)
				.receiversAccountNumber(322983792321L).amount(-100).senderPinNumber(1726).build();
		
		assertThrows(IllegalArgumentException.class, () -> transactionService.transferMoney(paramObj));
	}
	
	@Test
	void transferWithInvalidSenderAccount() throws AuthenticationException, InstanceAlreadyExistsException, InvalidAttributesException {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(7983402938020L)
				.receiversAccountNumber(322983792321L).amount(100).senderPinNumber(1726).build();
		when(accountRepo.findByAccountNumber(7983402938020L)).thenReturn(null);
		
		assertThrows(InvalidAttributesException.class, () -> transactionService.transferMoney(paramObj));
	}
	
	@Test
	void transferWithInvalidReceiverAccount() {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(798340293802L)
				.receiversAccountNumber(3229837923210L).amount(100).senderPinNumber(1726).build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		when(accountRepo.findByAccountNumber(3229837923210L)).thenReturn(null);
		
		assertThrows(InvalidAttributesException.class, () -> transactionService.transferMoney(paramObj));
	}
	
	@Test
	void transferWithInvalidPinLength() {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(798340293802L)
				.receiversAccountNumber(322983792321L).amount(100).senderPinNumber(17260).build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		
		assertThrows(AuthenticationException.class, () -> transactionService.transferMoney(paramObj));
	}
	
	@Test
	void transferWithInvalidPin() {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(798340293802L)
				.receiversAccountNumber(322983792321L).amount(100).senderPinNumber(1720).build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		
		assertThrows(AuthenticationException.class, () -> transactionService.transferMoney(paramObj));
	}
	
	@Test
	void transferWithInActiveAccount() {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(732948729709L)
				.receiversAccountNumber(322983792321L).amount(100).senderPinNumber(4527).build();
		when(accountRepo.findByAccountNumber(732948729709L)).thenReturn(accountObj3);
		
		assertThrows(InvalidAttributesException.class, () -> transactionService.transferMoney(paramObj));
	}
	
	@Test
	void transferWithInsufficientBalance() {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(798340293802L)
				.receiversAccountNumber(322983792321L).amount(100000).senderPinNumber(1726).build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		
		assertThrows(InvalidAttributesException.class, () -> transactionService.transferMoney(paramObj));
	}
	
	
	// deposit - positive test
	
	@Test
	void depositTest() throws InvalidAttributesException {
		Account accountObj = Account.builder().accountNumber(798340293802L).customerId(82498224L)
				.accountType(AccountType.SAVINGS).balance(5000).pinNumber(1726).isActive(true).build();
		DepositRequest paramObj = DepositRequest.builder().accountNumber(798340293802L).amount(100).build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj);
		
		Map<String, String> response = transactionService.depositMoney(paramObj);
		assertEquals("Available Balance: 5100", response.get("message"));
	}
	
	
	// withdraw - positive test
	
	@Test
	void withdrawTest() throws AuthenticationException, InvalidAttributesException {
		Account accountObj = Account.builder().accountNumber(798340293802L).customerId(82498224L)
				.accountType(AccountType.SAVINGS).balance(5000).pinNumber(1726).isActive(true).build();
		WithdrawRequest paramObj = WithdrawRequest.builder().accountNumber(798340293802L).amount(100)
				.pinNumber(1726).build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj);
		
		Map<String, String> response = transactionService.withdrawMoney(paramObj);
		assertEquals("Available Balance: 4900", response.get("message"));
	}
	
	// miniStatement - positive test
	
	@Test
	void getMiniStatementTest() throws AuthenticationException, InvalidAttributesException {
		List<Transaction> transactionList = Arrays.asList(transactionObj1);
		
		MiniStatementRequest paramObj = MiniStatementRequest.builder().accountNumber(798340293802L).pinNumber(1726)
				.build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		when(transactionRepo.findByAccountNumber(798340293802L)).thenReturn(transactionList);
		
		List<Transaction> response = transactionService.getMiniStatement(paramObj);
		assertEquals(500, response.get(0).getAmount());
	}
	
	
	// miniStatement - negative test cases
	
	@Test
	void getMiniStatementWithEmptyTransactions() throws AuthenticationException, InvalidAttributesException {
		List<Transaction> transactionList = new ArrayList<>();
		
		MiniStatementRequest paramObj = MiniStatementRequest.builder().accountNumber(798340293802L).pinNumber(1726)
				.build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		when(transactionRepo.findByAccountNumber(798340293802L)).thenReturn(transactionList);
		
		List<Transaction> response = transactionService.getMiniStatement(paramObj);
		assertEquals("[]", response.toString());
	}
	
}

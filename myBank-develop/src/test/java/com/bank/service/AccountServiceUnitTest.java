package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import com.bank.dto.CheckBalanceRequest;
import com.bank.dto.CreateAccountRequest;
import com.bank.dto.CreateAccountResponse;
import com.bank.dto.CreateCustomerWithAccountRequest;
import com.bank.dto.CreateCustomerWithAccountResponse;
import com.bank.dto.DisableRequest;
import com.bank.dto.GetAccountRequest;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.Address;
import com.bank.model.Customer;
import com.bank.model.DateOfBirth;
import com.bank.repository.mongo.AccountRepositoryMongo;
import com.bank.repository.mongo.CustomerRepositoryMongo;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountServiceUnitTest {
	
	@MockBean
	AccountRepositoryMongo accountRepo;
	
	@MockBean
	CustomerRepositoryMongo customerRepo;
	
	@Autowired
	AccountService accountService;
	
	
	DateOfBirth dobObj1 = null;
	DateOfBirth dobObj2 = null;
	
	Address addressObj1 = null;
	Address addressObj2 = null;
	
	Customer customerObj1 = null;
	Customer customerObj2 = null;
	
	Account accountObj1 = null;
	Account accountObj2 = null;
	Account accountObj3 = null;
	
	@BeforeAll
	void setUp() {
		
		dobObj1 = DateOfBirth.builder().day(12).month(11).year(1987).build();
		dobObj2 = DateOfBirth.builder().day(20).month(9).year(1995).build();
		
		addressObj1 = Address.builder().houseNumber("34-5/1").zipCode(30070).city("Mumbai").build();
		addressObj2 = Address.builder().houseNumber("43-8/3").zipCode(40050).city("Delhi").build();
		
		customerObj1 = Customer.builder().customerId(82498224L).customerName("John").phoneNumber(8470293024L)
				.adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj1).address(addressObj1).build();
		customerObj2 = Customer.builder().customerId(73298092L).customerName("Sam").phoneNumber(8247928734L)
				.adhaarNumber(734728370294L).email("sam@gmail.com").dob(dobObj2).address(addressObj2).build();
		
		accountObj1 = Account.builder().accountNumber(798340293802L).customerId(82498224L).accountType(AccountType.SAVINGS)
				.balance(5000).pinNumber(1726).isActive(true).build();
		accountObj2 = Account.builder().accountNumber(322983792321L).customerId(73298092L).accountType(AccountType.SAVINGS)
				.balance(4000).pinNumber(8746).isActive(true).build();
		accountObj3 = Account.builder().accountNumber(732948729709L).customerId(53480392L).accountType(AccountType.CURRENT)
				.balance(2000).pinNumber(4527).isActive(false).build();
		
	}
	
	// createCustomerWithAccount - positive test
	
	@Test
	void addCustomerWithAccountTest() throws InstanceAlreadyExistsException, JsonProcessingException {
		List<Customer> customerList = new ArrayList<>();
		customerList.add(customerObj2);
		
		when(customerRepo.findAll()).thenReturn(customerList);
		when(customerRepo.save(Mockito.any(Customer.class))).thenAnswer(i -> i.getArguments()[0]);
		when(accountRepo.save(Mockito.any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

		
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj1)
				.address(addressObj1).accountType(AccountType.SAVINGS).balance(5000).build();
		
		CreateCustomerWithAccountResponse response = accountService.createCustomerWithAccount(paramObj);
		assertEquals("John", response.getCustomerName());
	}
	
	
	// addCustomerWithAccount - negative test cases
	
	@Test
	void addWithAlreadyExistingAdhaarNumber() throws InstanceAlreadyExistsException {
		List<Customer> customerList = new ArrayList<>();
		customerList.add(customerObj1);
		
		when(customerRepo.findAll()).thenReturn(customerList);
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj1)
				.address(addressObj1).accountType(AccountType.SAVINGS).balance(5000).build();

		assertThrows(InstanceAlreadyExistsException.class,  () -> accountService.createCustomerWithAccount(paramObj));
	}
	
	// createAccount test
	
	@Test
	void createAccountTest() {
		List<Customer> customerList = new ArrayList<>();
		customerList.add(customerObj1);
		
		when(customerRepo.findAll()).thenReturn(customerList);
		CreateAccountRequest paramObj = CreateAccountRequest.builder().customerId(82498224L).accountType(AccountType.SAVINGS)
				.balance(3000).build();
		
		CreateAccountResponse response = accountService.createAccount(paramObj);
		assertEquals("John", response.getCustomerName());
	}
	
	// createAccount - negative test cases
	
	@Test
	void createAccountWithIncorrectCustomerIdLength() {
		CreateAccountRequest paramObj = CreateAccountRequest.builder().customerId(824982240L).accountType(AccountType.SAVINGS)
				.balance(2000).build();
		
		assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(paramObj));
	}
	
	@Test
	void creatingAccountWithoutCustomer() {
		List<Customer> customerList = new ArrayList<>();
		customerList.add(customerObj2);
		
		when(customerRepo.findAll()).thenReturn(customerList);
		CreateAccountRequest paramObj = CreateAccountRequest.builder().customerId(82498224L).accountType(AccountType.SAVINGS)
				.balance(3000).build();
		
		assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(paramObj));
	}
	
	// getList test

	@Test
	void getListTest() {
		List<Account> accountList = new ArrayList<>();
		accountList.add(accountObj1);
		
		when(accountRepo.findAll()).thenReturn(accountList);	
		GetAccountRequest paramObj = GetAccountRequest.builder().customerId(82498224L).build();
		
		List<Account> response = accountService.getList(paramObj);
		assertEquals(5000, response.get(0).getBalance());
	}
	
	// getList - negative test cases
	
	@Test
	void getListWithEmptyAccount() {
		GetAccountRequest paramObj = GetAccountRequest.builder().customerId(82498224L).build();
		
		List<Account> response = accountService.getList(paramObj);
		assertEquals("[]", response.toString());
	}
	
	
	// balanceCheck
	
	@Test
	void balanceCheck() throws AuthenticationException, InvalidAttributesException {
		CheckBalanceRequest paramObj = CheckBalanceRequest.builder().accountNumber(798340293802L).pinNumber(1726)
				.build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		
		Map<String, String> response = accountService.balanceCheck(paramObj);
		assertEquals("Available Balance: 5000", response.get("message"));
	}
	
	// balanceCheck - negative test cases
	
	@Test
	void balanceCheckWithInvalidAccountNumber() {
		CheckBalanceRequest paramObj = CheckBalanceRequest.builder().accountNumber(7983402938020L).pinNumber(1726)
				.build();
		when(accountRepo.findByAccountNumber(7983402938020L)).thenReturn(null);
		
		assertThrows(InvalidAttributesException.class, () -> accountService.balanceCheck(paramObj));
	}
	
	@Test
	void balanceCheckWithInvalidPinLength() {
		CheckBalanceRequest paramObj = CheckBalanceRequest.builder().accountNumber(798340293802L).pinNumber(17260)
				.build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		
		assertThrows(AuthenticationException.class, () -> accountService.balanceCheck(paramObj));
	}
	
	@Test
	void balanceCheckWithInvalidPin() {
		CheckBalanceRequest paramObj = CheckBalanceRequest.builder().accountNumber(798340293802L).pinNumber(1725)
				.build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		
		assertThrows(AuthenticationException.class, () -> accountService.balanceCheck(paramObj));
	}
	
	@Test
	void balanceCheckWithInActiveAccount() {
		CheckBalanceRequest paramObj = CheckBalanceRequest.builder().accountNumber(732948729709L).pinNumber(4527)
				.build();
		when(accountRepo.findByAccountNumber(732948729709L)).thenReturn(accountObj3);
		
		assertThrows(InvalidAttributesException.class, () -> accountService.balanceCheck(paramObj));
	}
	
	// disableAccount
	
	void disable() throws AuthenticationException, InvalidAttributesException {
		DisableRequest paramObj = DisableRequest.builder().accountNumber(798340293802L).pinNumber(1726).build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj1);
		
		Map<String, String> response = accountService.disable(paramObj);
		assertEquals("Success", response.get("message"));
	}
}

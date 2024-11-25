package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
import com.bank.model.DateOfBirth;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerUnitTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	AccountService accountService;
	
	@MockBean
	CustomerService customerService;
	
	
	DateOfBirth dobObj = null;
	Address addressObj = null;
	Account accountObj = null;
	
	@BeforeAll
	void setUp() {
		dobObj = DateOfBirth.builder().day(12).month(11).year(1987).build();
				
		addressObj = Address.builder().houseNumber("34-5/1").zipCode(30070).city("Mumbai").build();
				
		accountObj = Account.builder().accountNumber(798340293802L).customerId(82498224L)
				.accountType(AccountType.SAVINGS).balance(5000).pinNumber(1726).isActive(true).build();
	}
	
	
	// addCustomerWithAccount - positive tests
	
	@Test
	void addCustomerWithAccountTest() throws Exception {
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		CreateCustomerWithAccountResponse expected = CreateCustomerWithAccountResponse.builder()
				.customerName("John").customerId(82498224L).accountNumber(798340293802L).pinNumber(1726).build();
		when(accountService.createCustomerWithAccount(Mockito.any(CreateCustomerWithAccountRequest.class)))
			.thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerName", is("John")));
	}
	
	
	// addCustomerWithAccount - negative test cases
	
	@Test
	void addWithAlreadyExistingAdhaarNumber() throws Exception {
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		when(accountService.createCustomerWithAccount(Mockito.any(CreateCustomerWithAccountRequest.class)))
			.thenThrow(InstanceAlreadyExistsException.class);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error", is("Already Exist")));
	}
	
	@Test
	void addWithInvalidBirthDay() throws Exception {
		DateOfBirth dobObj = DateOfBirth.builder().day(35).month(11).year(1987).build();
		
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid birth day")));
	}
	
	@Test
	void addWithInvalidMonth() throws Exception {
		DateOfBirth dobObj = DateOfBirth.builder().day(25).month(22).year(1987).build();
		
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid birth month")));
		
	}	
	
	
	@Test
	void addWithInvalidAge() throws Exception {
		DateOfBirth dobObj = DateOfBirth.builder().day(25).month(12).year(2020).build();
		
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid Age, Must be greater than or equal to 18")));
	}
	
	@Test
	void addWithInvalidYear() throws Exception {
		DateOfBirth dobObj = DateOfBirth.builder().day(25).month(12).year(1900).build();
		
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid year")));
	}
	
	@Test
	void addWithInvalidPhoneNumber() throws Exception {
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(84702930240L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid Phone number")));
	}
	
	@Test
	void addWithInvalidAdhaarNumberLength() throws Exception {
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(3309709384230L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid Adhaar number")));
	}
	
	// Add/Create Account - positive Test
	
	@Test
	void addAccountTest() throws Exception {
		CreateAccountRequest paramObj = CreateAccountRequest.builder().customerId(82498224L)
				.accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		CreateAccountResponse expected = CreateAccountResponse.builder().customerName("John")
				.accountNumber(798340293802L).customerId(82498224L).build();
		when(accountService.createAccount(Mockito.any(CreateAccountRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
		.post("/api/accounts/add-account")
		.content(content)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.customerName", is("John")))
		.andExpect(jsonPath("$.accountNumber", is(798340293802L)));
	}
	
	// Add/Create Account - negative test cases
	
	@Test
	void addAccountWithInvalidCustomerId() throws Exception {
		CreateAccountRequest paramObj = CreateAccountRequest.builder().customerId(824982240L)
				.accountType(AccountType.SAVINGS).balance(5000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		when(accountService.createAccount(Mockito.any(CreateAccountRequest.class)))
			.thenThrow(IllegalArgumentException.class);
		
		mockMvc.perform(MockMvcRequestBuilders
		.post("/api/accounts/add-account")
		.content(content)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.error", is("Bad Request")));
	}
	
	
	// listAccount - positive test
	
	@Test
	void listAccountTest() throws Exception {
		List<Account> expected = Arrays.asList(accountObj);
		
		GetAccountRequest paramObj = GetAccountRequest.builder().customerId(82498224L).build();
		String content = objectMapper.writeValueAsString(paramObj);
		when(accountService.getList(Mockito.any(GetAccountRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/get-accounts")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].balance", is(5000)))
				.andExpect(jsonPath("$[0].pinNumber", is(1726)));
	}
	
	// balance check - positive test
	
	@Test
	void balanceCheckTest() throws Exception {
		CheckBalanceRequest paramObj = CheckBalanceRequest.builder().accountNumber(798340293802L).pinNumber(1726)
				.build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("message", "Available Balance: 5000");
		when(accountService.balanceCheck(Mockito.any(CheckBalanceRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/balance-check")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Available Balance: 5000")));
	}
	
	
	// balance check - negative test cases
	
	@Test
	void balanceCheckWithInvalidAccountNumber() throws Exception {
		CheckBalanceRequest paramObj = CheckBalanceRequest.builder().accountNumber(7983402938020L).pinNumber(1726)
				.build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		when(accountService.balanceCheck(Mockito.any(CheckBalanceRequest.class)))
			.thenThrow(InvalidAttributesException.class);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/balance-check")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error", is("Bad Request")));
	}
	
	@Test
	void balanceCheckWithInvalidPinNumber() throws Exception {
		CheckBalanceRequest paramObj = CheckBalanceRequest.builder().accountNumber(7983402938020L).pinNumber(172)
				.build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		when(accountService.balanceCheck(Mockito.any(CheckBalanceRequest.class)))
			.thenThrow(AuthenticationException.class);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/balance-check")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.error", is("Not Authorized")));
	}
	
	// disable - positive test
	
	@Test
	void disableTest() throws Exception {
		DisableRequest paramObj = DisableRequest.builder().accountNumber(798340293802L).pinNumber(1726).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("message", "Success");
		when(accountService.disable(Mockito.any(DisableRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/accounts/disable")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Success")));
	}
	
}

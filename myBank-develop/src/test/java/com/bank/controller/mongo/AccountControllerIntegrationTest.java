package com.bank.controller.mongo;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bank.MybankApplication;
import com.bank.dto.CheckBalanceRequest;
import com.bank.dto.CreateAccountRequest;
import com.bank.dto.CreateCustomerRequest;
import com.bank.dto.CreateCustomerWithAccountRequest;
import com.bank.dto.CreateCustomerWithAccountResponse;
import com.bank.dto.DeleteAccountRequest;
import com.bank.dto.DeleteCustomerRequest;
import com.bank.dto.DisableRequest;
import com.bank.dto.GetAccountRequest;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.Address;
import com.bank.model.Customer;
import com.bank.model.DateOfBirth;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = MybankApplication.class
		)
@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application.properties")
@TestPropertySource(locations = "classpath:mongo-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class AccountControllerIntegrationTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	CustomerRepository customerRepo;
	
	
	static long accountNo;
	static int pinNo;
	static long customerId;
	
	
	DateOfBirth dobObj1 = null;
	Address addressObj1 = null;
	
	@BeforeAll
	void setUp() throws Exception {
		dobObj1 = DateOfBirth.builder().day(12).month(11).year(1987).build();
		addressObj1 = Address.builder().houseNumber("34-5/1").zipCode(30070).city("Delhi").build();
		
		// Creating Customer with Account
		CreateCustomerWithAccountRequest paramObj = CreateCustomerWithAccountRequest.builder().customerName("Michael")
				.phoneNumber(8470293024L).adhaarNumber(330970448423L).email("mike@gmail.com").dob(dobObj1)
				.address(addressObj1).accountType(AccountType.SAVINGS).balance(4000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		CreateCustomerWithAccountResponse accountDetails = objectMapper.readValue(response.getResponse().getContentAsString(), 
				CreateCustomerWithAccountResponse.class);
		long accountNumber = accountDetails.getAccountNumber();
		int pinNumber = accountDetails.getPinNumber();
		long cusId = accountDetails.getCustomerId();
		
		accountNo = accountNumber;
		pinNo = pinNumber;
		customerId = cusId;
	}
	
	
	@Test
	@Order(1)
	void addCustomer() throws Exception {
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("John")
				.phoneNumber(8709238402L).adhaarNumber(798843509438L).email("john@gmail.com").dob(dobObj1)
				.address(addressObj1).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/customers")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerName", is("John")))
				.andExpect(jsonPath("$.adhaarNumber", is(798843509438L)));
	}
	
	@Test
	@Order(2)
	void addAccount() throws Exception {
		CreateAccountRequest paramObj = CreateAccountRequest.builder().customerId(customerId)
				.accountType(AccountType.SAVINGS).balance(4000).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/add-account")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerName", is("Michael")));
	}
	
	@Test
	@Order(3)
	void listTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()) 
				.andExpect(jsonPath("$[*].customerName").value(Matchers.hasItem("Michael")))
				.andExpect(jsonPath("$[*].address.city").value(Matchers.hasItem("Delhi")));
	}
	
	@Test
	@Order(4)
	void getAccount() throws Exception {
		GetAccountRequest paramObj = GetAccountRequest.builder().customerId(customerId).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/get-accounts")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].balance", is(4000)));
	}
	
	@Test
	@Order(5)
	void balanceCheckTest() throws Exception {
		CheckBalanceRequest paramObj = new CheckBalanceRequest(accountNo, pinNo);
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/balance-check")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Available Balance: 4000")));
	}
	
	@Test
	@Order(6)
	void disableAccountTest() throws Exception {
		DisableRequest paramObj = new DisableRequest(accountNo, pinNo);
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/accounts/disable")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Success")));
	}
	
	
	@Test
	@Order(7)
	void deleteLastTwoCustomer() throws Exception {
		List<Customer> customerList = customerRepo.findAll();
		int cusLastIndex = customerList.size() - 1;
		Customer lastCusObj = customerList.get(cusLastIndex);
		DeleteCustomerRequest paramObj1 = DeleteCustomerRequest.builder().customerId(lastCusObj.getCustomerId()).build();
		String content1 = objectMapper.writeValueAsString(paramObj1);
		
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/customers")
				.content(content1)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		int cusSecondLastIndex = customerList.size() - 2;
		Customer lastSecondCusObj = customerList.get(cusSecondLastIndex);
		DeleteCustomerRequest paramObj2 = DeleteCustomerRequest.builder().customerId(lastSecondCusObj.getCustomerId()).build();
		String content2 = objectMapper.writeValueAsString(paramObj2);
		
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/customers")
				.content(content2)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	
	@Test
	@Order(8)
	void deleteLastTwoAccount() throws Exception {
		List<Account> accountList = accountRepo.findAll();
		int accLastIndex = accountList.size() - 1;
		Account lastAccObj = accountList.get(accLastIndex);
		DeleteAccountRequest paramObj1 = DeleteAccountRequest.builder().accountNumber(lastAccObj.getAccountNumber()).build();
		String content1 = objectMapper.writeValueAsString(paramObj1);
		
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/accounts")
				.content(content1)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		int accLastSecondIndex = accountList.size() - 2;
		Account lastSecondAccObj = accountList.get(accLastSecondIndex);
		DeleteAccountRequest paramObj2 = DeleteAccountRequest.builder().accountNumber(lastSecondAccObj.getAccountNumber()).build();
		String content2 = objectMapper.writeValueAsString(paramObj2);
		
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/accounts")
				.content(content2)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	
	/*
	
	@AfterAll
	void tearDownMongo() {
		// removing last two customers
		List<Customer> customerList = customerRepo.findAll();
		int cusLastIndex = customerList.size() - 1;
		Customer lastCusObj = customerList.get(cusLastIndex);
		customerRepo.deleteById(lastCusObj.getCustomerId());
		
		int cusSecondLastIndex = customerList.size() - 2;
		Customer lastSecondCusObj = customerList.get(cusSecondLastIndex);
		customerRepo.deleteById(lastSecondCusObj.getCustomerId());
		
		
		// removing last two accounts
		List<Account> accountList = accountRepo.findAll();
		int accLastIndex = accountList.size() - 1;
		Account lastAccObj = accountList.get(accLastIndex);
		accountRepo.deleteById(lastAccObj.getAccountNumber());
		
		int accLastSecondIndex = accountList.size() - 2;
		Account lastSecondAccObj = accountList.get(accLastSecondIndex);
		accountRepo.deleteById(lastSecondAccObj.getAccountNumber());
	}
	
	*/
	
	/*
	
	@AfterAll
	void tearDownJpa() throws Exception {
		
		// removing last customer two customers
		List<Customer> customerList = customerRepo.findAll();
		int cusLastIndex = customerList.size() - 1;
		Customer lastCusObj = customerList.get(cusLastIndex);
		utility.deleteCustomer(lastCusObj.getCustomerId());
				
		int cusSecondLastIndex = customerList.size() - 2;
		Customer lastSecondCusObj = customerList.get(cusSecondLastIndex);
		utility.deleteCustomer(lastSecondCusObj.getCustomerId());
//		
		// removing last two accounts
		List<Account> accountList = accountRepo.findAll();
		int accLastIndex = accountList.size() - 1;
		Account lastAccObj = accountList.get(accLastIndex);
		utility.deleteAccount(lastAccObj.getAccountNumber());
//		DeleteAccountRequest accParamObj1 = DeleteAccountRequest.builder().accountNumber(lastAccObj.getAccountNumber()).build();
//		accountService.deleteAccount(accParamObj1);
							
		int accLastSecondIndex = accountList.size() - 2;
		Account lastSecondAccObj = accountList.get(accLastSecondIndex);
		utility.deleteAccount(lastSecondAccObj.getAccountNumber());	
//		accountRepo.deleteAccount(lastSecondAccObj.getAccountNumber());
	 
	}
	
	*/
	
}

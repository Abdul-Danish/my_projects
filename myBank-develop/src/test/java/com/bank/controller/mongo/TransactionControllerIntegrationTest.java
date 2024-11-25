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
import com.bank.dto.CreateCustomerWithAccountRequest;
import com.bank.dto.CreateCustomerWithAccountResponse;
import com.bank.dto.DeleteAccountRequest;
import com.bank.dto.DeleteCustomerRequest;
import com.bank.dto.DeleteTransactionRequest;
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
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;
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
class TransactionControllerIntegrationTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
	TransactionRepository transactionRepo;
	

	
	static long senderAccountNo;
	static int senderPinNo;
	static long receiverAccountNo;
	
	DateOfBirth dobObj1 = null;
	DateOfBirth dobObj2 = null;
	
	Address addressObj1 = null;
	Address addressObj2 = null;
	
	@BeforeAll
	void setUp() throws Exception {
		dobObj1 = DateOfBirth.builder().day(12).month(11).year(1987).build();
		dobObj2 = DateOfBirth.builder().day(13).month(11).year(1989).build();
		
		addressObj1 = Address.builder().houseNumber("34-5/1").zipCode(30070).city("Hyderabad").build();
		addressObj2 = Address.builder().houseNumber("21-3/4").zipCode(50030).city("Mumbai").build();
		
		// Creating customer
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
		senderAccountNo = accountDetails.getAccountNumber();
		senderPinNo = accountDetails.getPinNumber();
		
		// Creating Another Customer
		CreateCustomerWithAccountRequest paramObj2 = CreateCustomerWithAccountRequest.builder().customerName("John")
				.phoneNumber(7394857970L).adhaarNumber(983749239482L).email("john@gmail.com").dob(dobObj2)
				.address(addressObj2).accountType(AccountType.SAVINGS).balance(3000).build();
		String content2 = objectMapper.writeValueAsString(paramObj2);
		
		MvcResult response2 = mockMvc.perform(MockMvcRequestBuilders
				.post("/api/accounts/create")
				.content(content2)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		CreateCustomerWithAccountResponse accountDetails2 = objectMapper.readValue(response2.getResponse().getContentAsString(), 
				CreateCustomerWithAccountResponse.class);  
		receiverAccountNo = accountDetails2.getAccountNumber();
	}
	
	@Test
	@Order(1)
	void transferTest() throws Exception {
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(senderAccountNo)
				.receiversAccountNumber(receiverAccountNo).amount(100).senderPinNumber(senderPinNo).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/transactions/transfer")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Available Balance: 3100")));
	}

	@Test
	@Order(2)
	void withdrawTest() throws Exception {
		WithdrawRequest paramObj = WithdrawRequest.builder().accountNumber(senderAccountNo).amount(100)
				.pinNumber(senderPinNo).build();
		String content = objectMapper.writeValueAsString(paramObj);

		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/transactions/withdraw")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Available Balance: 3800")));
	}
	
	@Test
	@Order(3)
	void depositTest() throws Exception {
		DepositRequest paramObj = DepositRequest.builder().accountNumber(senderAccountNo).amount(500)
				.build();
		String content = objectMapper.writeValueAsString(paramObj);

		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/transactions/deposit")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Available Balance: 4300")));
	}
	
	@Test
	@Order(4)
	void miniStatementTest() throws Exception {
		MiniStatementRequest paramObj = MiniStatementRequest.builder().accountNumber(senderAccountNo)
				.pinNumber(senderPinNo).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/transactions/mini-statement")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].amount").value(Matchers.hasItem(500)));
	}
	
	@Test
	@Order(5)
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
	@Order(6)
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

	@Test
	@Order(7)
	void deleteLastFourTransaction() throws Exception {
		
		List<Transaction> transactionList = transactionRepo.findAll();
		for (int i=1; i<=4; i++) {
			int lastTransIndex = transactionList.size() - i;
			Transaction lastTransObj = transactionList.get(lastTransIndex);
			DeleteTransactionRequest paramObj = DeleteTransactionRequest.builder().transactionId(lastTransObj.getTransactionId()).build();
			String content = objectMapper.writeValueAsString(paramObj);
			
			mockMvc.perform(MockMvcRequestBuilders
					.delete("/api/transactions")
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
		}
		
	}
	
	
	/*
	
	@AfterAll
	void tearDownMongo() {
		// removing last two customer
		List<Customer> customerList = customerRepo.findAll();
		int cusLastIndex = customerList.size() - 1;
		Customer lastCusObj = customerList.get(cusLastIndex);
		customerRepo.deleteById(lastCusObj.getCustomerId());
		
		int cusLastSecondIndex = customerList.size() - 2;
		Customer lastSecondCusObj = customerList.get(cusLastSecondIndex);
		customerRepo.deleteById(lastSecondCusObj.getCustomerId());
				
		// removing last two account
		List<Account> accountList = accountRepo.findAll();
		int accLastIndex = accountList.size() - 1;
		Account lastAccObj = accountList.get(accLastIndex);
		accountRepo.deleteById(lastAccObj.getAccountNumber());
		
		int accLastSecondIndex = accountList.size() - 2;
		Account lastSecondAccObj = accountList.get(accLastSecondIndex);
		accountRepo.deleteById(lastSecondAccObj.getAccountNumber());
		
		// removing last four transactions
		List<Transaction> transactionList = transactionRepo.findAll();
		int lastTransIndex = transactionList.size() - 1;
		Transaction lastTransObj = transactionList.get(lastTransIndex);
		transactionRepo.deleteById(lastTransObj.getTransactionId());
		
		int lastSecondTransIndex = transactionList.size() - 2;
		Transaction lastSecondTransObj = transactionList.get(lastSecondTransIndex);
		transactionRepo.deleteById(lastSecondTransObj.getTransactionId());
		
		int lastThirdTransIndex = transactionList.size() - 3;
		Transaction lastThirdTransObj = transactionList.get(lastThirdTransIndex);
		transactionRepo.deleteById(lastThirdTransObj.getTransactionId());
		
		int lastFourthTransIndex = transactionList.size() - 4;
		Transaction lastFourthTransObj = transactionList.get(lastFourthTransIndex);
		transactionRepo.deleteById(lastFourthTransObj.getTransactionId());
	}

	*/
	
	/*
	
	@AfterAll
	void tearDownJpa() throws Exception {
		// removing last two customer
		List<Customer> customerList = customerRepo.findAll();
		int cusLastIndex = customerList.size() - 1;
		Customer lastCusObj = customerList.get(cusLastIndex);
		utility.deleteCustomer(lastCusObj.getCustomerId());
		
		int cusLastSecondIndex = customerList.size() - 2;
		Customer lastSecondCusObj = customerList.get(cusLastSecondIndex);
		utility.deleteCustomer(lastSecondCusObj.getCustomerId());
		
		// removing last two account
		List<Account> accountList = accountRepo.findAll();
		int accLastIndex = accountList.size() - 1;
		Account lastAccObj = accountList.get(accLastIndex);
		utility.deleteAccount(lastAccObj.getAccountNumber());
		
		int accLastSecondIndex = accountList.size() - 2;
		Account lastSecondAccObj = accountList.get(accLastSecondIndex);
		utility.deleteAccount(lastSecondAccObj.getAccountNumber());
		
		// removing last four transactions
		List<Transaction> transactionList = transactionRepo.findAll();
		int lastTransIndex = transactionList.size() - 1;
		Transaction lastTransObj = transactionList.get(lastTransIndex);
		utility.deleteTransaction(lastTransObj.getTransactionId());
		
		int lastSecondTransIndex = transactionList.size() - 2;
		Transaction lastSecondTransObj = transactionList.get(lastSecondTransIndex);
		utility.deleteTransaction(lastSecondTransObj.getTransactionId());
		
		int lastThirdTransIndex = transactionList.size() - 3;
		Transaction lastThirdTransObj = transactionList.get(lastThirdTransIndex);
		utility.deleteTransaction(lastThirdTransObj.getTransactionId());
		
		int lastFourthTransIndex = transactionList.size() - 4;
		Transaction lastFourthTransObj = transactionList.get(lastFourthTransIndex);
		utility.deleteTransaction(lastFourthTransObj.getTransactionId());
	}

	*/
}

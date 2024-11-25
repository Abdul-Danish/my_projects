package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.InvalidAttributesException;

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

import com.bank.dto.DepositRequest;
import com.bank.dto.MiniStatementRequest;
import com.bank.dto.TransferRequest;
import com.bank.dto.WithdrawRequest;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionControllerUnitTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	TransactionService transactionService;
	
	
	Transaction transactionObj = null;
	
	@BeforeAll
	void setUp() {
		transactionObj = Transaction.builder().transactionId(83487938L).accountNumber(798340293802L)
				.customerId(82498224L).amount(100)
				.transactionType(TransactionType.WITHDRAW).build();
	}
	
	
	// transfer - positive test
	
	@Test
	void transferTest() throws Exception {
		long senderAccountNumber = 798340293802L; // customerObj1
		long receiverAccountNumber = 322983792321L; // customerObj2
		int senderPinNumber = 1726;
		TransferRequest paramObj = TransferRequest.builder().sendersAccountNumber(senderAccountNumber)
				.receiversAccountNumber(receiverAccountNumber).amount(100).senderPinNumber(senderPinNumber)
				.build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("message", "Available Balance: 4100");
		when(transactionService.transferMoney(Mockito.any(TransferRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/transactions/transfer")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Available Balance: 4100")));
	}
	
	
	// deposit - positive test
	
	@Test
	void depositTest() throws Exception {
		DepositRequest paramObj = DepositRequest.builder().accountNumber(798340293802L).amount(100).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("message", "Available Balance: 5100");
		when(transactionService.depositMoney(Mockito.any(DepositRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/transactions/deposit")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Available Balance: 5100")));
	}
	
	// withdraw - positive test
	
	@Test
	void withdrawTest() throws Exception {
		WithdrawRequest paramObj = WithdrawRequest.builder().accountNumber(798340293802L).amount(100)
				.pinNumber(1726).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("message", "Available Balance: 4900");
		when(transactionService.withdrawMoney(Mockito.any(WithdrawRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/transactions/withdraw")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Available Balance: 4900")));
	}
	
	
	// miniStatement - positive test
	
	@Test
	void miniStatementTest() throws Exception {
		List<Transaction> expected = Arrays.asList(transactionObj);
		
		MiniStatementRequest paramObj = MiniStatementRequest.builder().accountNumber(798340293802L).pinNumber(1726)
				.build();
		String content = objectMapper.writeValueAsString(paramObj);
		when(transactionService.getMiniStatement(Mockito.any(MiniStatementRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/transactions/mini-statement")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].amount", is(100)))
				.andExpect(jsonPath("$[0].transactionType", is("WITHDRAW")));
	}
	
}

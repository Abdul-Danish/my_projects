package com.bank.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.annotation.Id;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bank.dto.CreateCustomerRequest;
import com.bank.dto.UpdatePhoneNumberRequest;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.Address;
import com.bank.model.Customer;
import com.bank.model.DateOfBirth;
import com.bank.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerUnitTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	CustomerService customerService;
	
	DateOfBirth dobObj = null;
	Address addressObj = null;
	Customer customerObj = null;
	Account accountObj = null;
	
	@BeforeAll
	void setUp() {
		dobObj = DateOfBirth.builder().day(12).month(11).year(1987).build();
		
		addressObj = Address.builder().houseNumber("34-5/1").zipCode(30070).city("Hyderabad").build();
		
		customerObj = Customer.builder().customerId(82498224L).customerName("John").phoneNumber(8470293024L)
				.adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj).address(addressObj).build();
		
		accountObj = Account.builder().accountNumber(798340293802L).customerId(82498224L)
				.accountType(AccountType.SAVINGS).balance(5000).pinNumber(3005).build();
	}

	// list - positive test

	@Test
	void listTest() throws Exception {
		List<Customer> expected = Arrays.asList(customerObj);
		when(customerService.getList()).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].customerName", is("John")))
				.andExpect(jsonPath("$[0].phoneNumber", is(8470293024L)))
				.andExpect(jsonPath("$[0].address.city", is("Hyderabad")));
	}
	
	// add - positive test
	
	@Test
	void addCustomerTest() throws Exception {
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("Neon").phoneNumber(7975039432L)
				.adhaarNumber(323984098324L).email("neon@gmail.com").dob(dobObj).address(addressObj).build();
		String content = objectMapper.writeValueAsString(paramObj);

		Customer expected = Customer.builder().customerId(79348532L).customerName("Neon").phoneNumber(7975039432L)
				.adhaarNumber(323984098324L).email("neon@gmail.com").dob(dobObj).address(addressObj).build();
		when(customerService.createCustomer(Mockito.any(CreateCustomerRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/customers")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.phoneNumber", is(7975039432L)))
				.andExpect(jsonPath("$.customerName", is("Neon")));
	}
	
	// add - negative test cases
	
	@Test
	void addWithInvalidBirthDay() throws Exception {
		DateOfBirth dobObj = DateOfBirth.builder().day(35).month(11).year(1987).build();
		
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/customers")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid birth day")));
	}
	
	@Test
	void addWithInvalidMonth() throws Exception {
		DateOfBirth dobObj = DateOfBirth.builder().day(25).month(22).year(1987).build();
		
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/customers")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid birth month")));
		
	}	
	
	
	@Test
	void addWithInvalidAge() throws Exception {
		DateOfBirth dobObj = DateOfBirth.builder().day(25).month(12).year(2020).build();
		
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/customers")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid Age, Must be greater than or equal to 18")));
	}
	
	@Test
	void addWithInvalidYear() throws Exception {
		DateOfBirth dobObj = DateOfBirth.builder().day(25).month(12).year(1900).build();
		
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/customers")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid year")));
	}
	
	@Test
	void addWithInvalidPhoneNumber() throws Exception {
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("John")
				.phoneNumber(84702930240L).adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/customers")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid Phone number")));
	}
	
	@Test
	void addWithInvalidAdhaarNumberLength() throws Exception {
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("John")
				.phoneNumber(8470293024L).adhaarNumber(3309709384230L).email("john@gmail.com").dob(dobObj)
				.address(addressObj).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/customers")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("['message']", is("Invalid Adhaar number")));
	}

	
	// updatePhoneNumer - positive test
	
	@Test
	void updatePhoneNumberTest() throws Exception {
		UpdatePhoneNumberRequest paramObj = UpdatePhoneNumberRequest.builder().accountNumber(798340293802L)
				.phoneNumber(8329089082L).build();
		String content = objectMapper.writeValueAsString(paramObj);
		
		Map<String, String> expected = new HashMap<>();
		expected.put("message", "Updated Successfully!");
		when(customerService.updatePhoneNumber(Mockito.any(UpdatePhoneNumberRequest.class))).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/customers/phone-number")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("['message']", is("Updated Successfully!")));
	}
	
	
}

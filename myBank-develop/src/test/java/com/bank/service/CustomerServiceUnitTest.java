package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.InvalidAttributesException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bank.dto.CreateCustomerRequest;
import com.bank.dto.UpdatePhoneNumberRequest;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.Address;
import com.bank.model.Customer;
import com.bank.model.DateOfBirth;
import com.bank.repository.mongo.AccountRepositoryMongo;
import com.bank.repository.mongo.CustomerRepositoryMongo;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerServiceUnitTest {
	
	@Autowired
	CustomerService customerService;

	@MockBean
	CustomerRepositoryMongo customerRepo;

	@MockBean
	AccountRepositoryMongo accountRepo;
	
	
	DateOfBirth dobObj = null;

	Address addressObj = null;

	Customer customerObj = null;
	
	Account accountObj = null;
	
	@BeforeAll
	void setUp() {
		dobObj = DateOfBirth.builder().day(12).month(11).year(1987).build();
		
		addressObj = Address.builder().houseNumber("34-5/1").zipCode(30070).city("Hyderaad").build();
		
		customerObj = Customer.builder().customerId(82498224L).customerName("John").phoneNumber(8470293024L)
				.adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj).address(addressObj).build();
		
		accountObj = Account.builder().accountNumber(798340293802L).customerId(82498224L)
				.accountType(AccountType.SAVINGS).balance(5000).pinNumber(3005).isActive(true).build();
	}

	
	// getList - positive test
	
	@Test
	void getListTest() {
		List<Account> accountList = new ArrayList<>();
		accountList.add(accountObj);
		when(accountRepo.findAll()).thenReturn(accountList);
		when(customerRepo.findByCustomerId(82498224L)).thenReturn(customerObj);
		
		List<Customer> response = customerService.getList();
		assertEquals("john@gmail.com", response.get(0).getEmail());
	}
	
	// getList - negative tests
	
	@Test
	void getListWithEmptyAccout() {
		List<Customer> response = customerService.getList();
		assertEquals("[]", response.toString());
	}
	
	
	// createCustomer - positive test
	
	@Test
	void createCustomerTest() throws InstanceAlreadyExistsException {
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("John").phoneNumber(8470293024L)
				.adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj).address(addressObj).build();
		when(customerRepo.save(customerObj)).thenReturn(customerObj);
		
		Customer response = customerService.createCustomer(paramObj);
		assertEquals("John", response.getCustomerName());
		assertEquals(8470293024L, response.getPhoneNumber());
	}
	
	// createCustomer - negative test cases
	
	@Test
	void createCustomerWithAlreadyExistingAdhaarNumber() throws InstanceAlreadyExistsException {
		List<Customer> customerList = new ArrayList<>();
		customerList.add(customerObj);
		
		CreateCustomerRequest paramObj = CreateCustomerRequest.builder().customerName("John").phoneNumber(8470293024L)
				.adhaarNumber(330970938423L).email("john@gmail.com").dob(dobObj).address(addressObj).build();
		when(customerRepo.findAll()).thenReturn(customerList);
		
		assertThrows(InstanceAlreadyExistsException.class, () -> customerService.createCustomer(paramObj));
	}
	
	// updatePhoneNumber - positive test
	
	@Test
	void updatePhoneNumberTest() throws InvalidAttributesException {
		UpdatePhoneNumberRequest paramObj = UpdatePhoneNumberRequest.builder().accountNumber(798340293802L)
				.phoneNumber(7987403299L).build();
		when(accountRepo.findByAccountNumber(798340293802L)).thenReturn(accountObj);
		when(customerRepo.findByCustomerId(82498224L)).thenReturn(customerObj);
		
		Map<String, String> response = customerService.updatePhoneNumber(paramObj);
		assertEquals("Updated Successfully!", response.get("message"));
	}
	
}

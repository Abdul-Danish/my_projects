package com.bank.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.InvalidAttributesException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.CreateCustomerRequest;
import com.bank.dto.DeleteCustomerRequest;
import com.bank.dto.UpdatePhoneNumberRequest;
import com.bank.model.Customer;
import com.bank.model.DateOfBirth;
import com.bank.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ControllerUtility controllerUtil;
	
	DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	private LocalDateTime now = LocalDateTime.now();
	
	
	@GetMapping()
	public List<Customer> list() {
		return customerService.getList();
	}
	
	@PostMapping()
	public Customer addCustomer(@RequestBody CreateCustomerRequest createCustomerRequestObj) throws InstanceAlreadyExistsException {
		
		// checking for valid date and month in date-of-birth
		DateOfBirth dateOfBirth = createCustomerRequestObj.getDob();
		controllerUtil.checkForValidDayAndMonth(dateOfBirth.getDay(), dateOfBirth.getMonth());

		// getting current year and checking for valid age and year
		String currentDate = date.format(now);
		controllerUtil.checkForValidAgeAndYear(currentDate, dateOfBirth);

		// checking for valid phone number length
		String stringPhoneNumber = String.valueOf(createCustomerRequestObj.getPhoneNumber());
		controllerUtil.checkForValidPhoneNumber(stringPhoneNumber);

		// checking for valid aadhar number length
		String stringAdhaarNumber = String.valueOf(createCustomerRequestObj.getAdhaarNumber());
		controllerUtil.checkForValidAdhaarLength(stringAdhaarNumber);
		
		return customerService.createCustomer(createCustomerRequestObj);
	}
	
	@PutMapping("/phone-number")
	public Map<String, String> updatePhoneNumber(@RequestBody UpdatePhoneNumberRequest updatePhRequestObj) throws InvalidAttributesException {
		return customerService.updatePhoneNumber(updatePhRequestObj);
	}
	
	@DeleteMapping()
	public Map<String, String> removeCustomer(@RequestBody DeleteCustomerRequest deleteCustomerRequestObj) {
		return customerService.deleteCustomer(deleteCustomerRequestObj);
	}
	
	@GetMapping("/test/email/{database}/{email}")
	public void testByEmail(@PathVariable String database, @PathVariable String email) {
	    customerService.testByEmail(database, email);
	}
	
	@GetMapping("/test/city/{database}/{city}")
    public void testByCity(@PathVariable String database, @PathVariable String city) {
        customerService.testByCity(database, city);
    }
}

package com.bank.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.InvalidAttributesException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.controller.ControllerUtility;
import com.bank.dto.CreateCustomerRequest;
import com.bank.dto.DeleteCustomerRequest;
import com.bank.dto.UpdatePhoneNumberRequest;
import com.bank.enums.Database;
import com.bank.factory.CustomerRepositoryFactory;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.jpa.CustomerRepositoryPostgres;
import com.bank.repository.mongo.CustomerRepositoryMongo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {

//	@Autowired
//	private CustomerProducer customerProducer;

//    @Autowired
    private CustomerRepositoryMongo customerRepositoryMongo;
    private CustomerRepositoryPostgres customerRepositoryPostgres;

    private CustomerRepository customerRepo;
    
    @Autowired
    private CustomerRepositoryFactory customerRepositoryFactory;
    
//    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private Utility utility;

    @Autowired
    private ControllerUtility controllerUtility;

    @Autowired
    public CustomerService(CustomerRepositoryMongo customerRepositoryMongo, CustomerRepositoryPostgres customerRepositoryPostgres) {
        this.customerRepositoryMongo = customerRepositoryMongo;
        this.customerRepositoryPostgres = customerRepositoryPostgres;
    }
    
    public void setDatabase(Database database) {
        if (database.equals(Database.POSTGRES)) {
            customerRepo = customerRepositoryPostgres;
        } else if (database.equals(Database.MONGO)) {
            customerRepo = customerRepositoryMongo;
        } 
    }
    
    // generating customer id
    Random myRandom = new Random();

    private long generateCustomerId() {
        StringBuilder customerIdString = new StringBuilder();
        for (int i = 1; i <= 8; i++) {
            customerIdString.append(myRandom.nextInt(8) + 1);
        }
        return Long.parseLong(customerIdString.toString());
    }

    // Getting all the customers with active account
    public List<Customer> getList() {

        List<Customer> activeAccountCustomerList = new ArrayList<>();
        List<Account> accountList = accountRepo.findAll();

        // Checking if the accountList is empty
        if (accountList.isEmpty()) {
            return activeAccountCustomerList;
        }

        for (Account obj : accountList) {
            if (obj.isActive()) {
                Customer customerObj = customerRepo.findByCustomerId(obj.getCustomerId());
                activeAccountCustomerList.add(customerObj);
            }
        }
        return activeAccountCustomerList;
    }

    public Customer createCustomer(CreateCustomerRequest createCustomerRequestObj) throws InstanceAlreadyExistsException {
//        setDatabase(createCustomerRequestObj.getDatabase());
        long customerId = generateCustomerId();

        // checking if account with current adhaar number already exist
//        List<Customer> customerList = customerRepo.findAll();
        /*
         * Test Code
         */
        List<Customer> customerList = customerRepositoryFactory.getDatabase(createCustomerRequestObj.getDatabase()).findAll();
        //
        for (Customer obj : customerList) {
            if (createCustomerRequestObj.getAdhaarNumber() == obj.getAdhaarNumber()) {
                throw new InstanceAlreadyExistsException("Account with current Adhaar Number already exist");
            }
        }

//		Customer customerObj = new Customer(customerId, createCustomerRequestObj.getCustomerName(),
//				createCustomerRequestObj.getPhoneNumber(), createCustomerRequestObj.getAdhaarNumber(),
//				createCustomerRequestObj.getEmail(), createCustomerRequestObj.getDob(), 
//				createCustomerRequestObj.getAddress());
        Customer customerObj = Customer.builder().customerId(customerId).customerName(createCustomerRequestObj.getCustomerName())
            .phoneNumber(createCustomerRequestObj.getPhoneNumber()).adhaarNumber(createCustomerRequestObj.getAdhaarNumber())
            .email(createCustomerRequestObj.getEmail()).dob(createCustomerRequestObj.getDob())
            .address(createCustomerRequestObj.getAddress()).build();
//        customerRepo.save(customerObj);
        customerRepositoryFactory.getDatabase(createCustomerRequestObj.getDatabase()).save(customerObj);

        log.debug("Customer Object after saving: {}", customerObj);

//		customerProducer.sendMessage(customerObj);

        return customerObj;
    }

    public Map<String, String> updatePhoneNumber(UpdatePhoneNumberRequest updatePhRequestObj) throws InvalidAttributesException {
        Account accountObj = accountRepo.findByAccountNumber(updatePhRequestObj.getAccountNumber());

        // checking for valid account number
        utility.checkForInvalidAccountNumber(accountObj);
        Customer customerObj = customerRepo.findByCustomerId(accountObj.getCustomerId());

        String stringPhoneNumber = String.valueOf(updatePhRequestObj.getPhoneNumber());

        // checking for valid phone number
        controllerUtility.checkForValidPhoneNumber(stringPhoneNumber);
        customerObj.setPhoneNumber(updatePhRequestObj.getPhoneNumber());
        customerRepo.save(customerObj);

        log.debug("Customer Object after update: {}", customerObj);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Updated Successfully!");
        return response;
    }

    public Map<String, String> deleteCustomer(DeleteCustomerRequest deleteCustomerRequestObj) {
        // checking for valid customer id
        utility.checkForCustomerIdLength(deleteCustomerRequestObj.getCustomerId());

        customerRepo.deleteById(deleteCustomerRequestObj.getCustomerId());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Customer Deleted Successfully");
        return response;
    }

    public void testByEmail(String database, String email) {
        List<Customer> customerByEmail = customerRepositoryFactory.getDatabase(Database.valueOf(database)).findByEmail(email);
        System.out.println("Emails: " + customerByEmail);
        System.out.println("Count: " + customerByEmail.size());

    }
    
    public void testByCity(String database, String city) {
        List<Customer> customerByCity = customerRepositoryFactory.getDatabase(Database.valueOf(database)).findByCity(city);
        System.out.println("Emails: " + customerByCity);
        System.out.println("Count: " + customerByCity.size());
    }
    
}

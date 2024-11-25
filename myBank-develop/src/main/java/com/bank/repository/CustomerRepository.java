package com.bank.repository;

import java.util.List;

import com.bank.model.Customer;

public interface CustomerRepository {

	List<Customer> findAll();
	
	Customer save(Customer entity);
	
	void deleteById(long customerId);
	
	
	Customer findByCustomerId(long customerId);
	
	public List<Customer> findByEmail(String email);
	
	public List<Customer> findByCity(String city);
}

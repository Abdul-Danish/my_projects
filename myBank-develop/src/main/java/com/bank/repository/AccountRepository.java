package com.bank.repository;

import java.util.List;

import com.bank.model.Account;
import com.bank.model.Customer;


public interface AccountRepository {

	Account save(Account entity);
	
	List<Account> findAll();
	
	void deleteById(long accountNumber);
	
	
	Account findByAccountNumber(long accountNumber);
	
	Customer findByCustomerId(long customerId);
}

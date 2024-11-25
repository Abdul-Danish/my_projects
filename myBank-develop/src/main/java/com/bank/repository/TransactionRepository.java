package com.bank.repository;

import java.util.List;

import com.bank.model.Transaction;

public interface TransactionRepository {
	
	Transaction save(Transaction entity);
	
	void deleteById(long transactionId);
	
	List<Transaction> findAll();
	
	List<Transaction> findByAccountNumber(long accountNumber);
}

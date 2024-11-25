package com.bank.model;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

class ModelTest {

	@Test
	void customerTest() {
		ToStringVerifier.forClass(Customer.builder().getClass()).verify();
	}
	
//	@Test
//	void accountTest() {
//		ToStringVerifier.forClass(Account.builder().getClass()).verify();
//	}

	@Test
	void addressTest() {
		ToStringVerifier.forClass(Address.builder().getClass()).verify();
	}
	
	@Test
	void dobTest() {
		ToStringVerifier.forClass(DateOfBirth.builder().getClass()).verify();
	}
	
	@Test
	void transactionTest() {
		ToStringVerifier.forClass(Transaction.builder().getClass()).verify();
	}
	
	
	
}

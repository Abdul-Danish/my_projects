package com.bank.dto;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

class DtoTest {

	@Test
	void createCustomerWithAccountRequestTest() {
		ToStringVerifier.forClass(CreateCustomerWithAccountRequest.builder().getClass()).verify();
	}

	@Test 
	void createCustomerWithAccountResponseTest() {
		ToStringVerifier.forClass(CreateCustomerWithAccountResponse.builder().getClass()).verify();
	}
	
	@Test
	void disableRequestTest() {
		ToStringVerifier.forClass(DisableRequest.builder().getClass()).verify();
	}
	
	@Test
	void createCustomerRequestTest() {
		ToStringVerifier.forClass(CreateCustomerRequest.builder().getClass()).verify();
	}
	
	@Test
	void deleteCustomerRequestTest() {
		ToStringVerifier.forClass(DeleteCustomerRequest.builder().getClass()).verify();
	}
	
	@Test
	void TransferRequestTest() {
		ToStringVerifier.forClass(TransferRequest.builder().getClass()).verify();
	}
	
	@Test
	void deleteTransactionRequestTest() {
		ToStringVerifier.forClass(DeleteTransactionRequest.builder().getClass()).verify();
	}
	
	@Test
	void createAccountRequestTest() {
		ToStringVerifier.forClass(CreateAccountRequest.builder().getClass()).verify();
	}
	
	@Test
	void createAccountResponseTest() {
		ToStringVerifier.forClass(CreateAccountResponse.builder().getClass()).verify();
	}
	
	@Test
	void deleteAccountRequestTest() {
		ToStringVerifier.forClass(DeleteAccountRequest.builder().getClass()).verify();
	}
		
	@Test
	void withdrawRequestTest() {
		ToStringVerifier.forClass(WithdrawRequest.builder().getClass()).verify();
	}
	
	@Test
	void checkBalanceRequestTest() {
		ToStringVerifier.forClass(CheckBalanceRequest.builder().getClass()).verify();
	}
	
	@Test
	void depositRequestTest() {
		ToStringVerifier.forClass(DepositRequest.builder().getClass()).verify();
	}
	
	@Test
	void updatePhoneNumberRequestTest() {
		ToStringVerifier.forClass(UpdatePhoneNumberRequest.builder().getClass()).verify();
	}
	
	@Test
	void miniStatementRequestTest() {
		ToStringVerifier.forClass(MiniStatementRequest.builder().getClass()).verify();
	}
	
	@Test
	void getAccountRequest() {
		ToStringVerifier.forClass(GetAccountRequest.builder().getClass()).verify();
	}
	
}

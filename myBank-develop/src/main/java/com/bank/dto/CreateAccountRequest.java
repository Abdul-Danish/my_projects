package com.bank.dto;

import com.bank.model.AccountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

	private long customerId;
	private AccountType accountType;
	private long balance;
	
	
}

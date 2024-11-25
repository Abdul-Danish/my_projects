package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateAccountResponse {

	private String customerName;
	private long accountNumber;
	private long customerId;
	
}

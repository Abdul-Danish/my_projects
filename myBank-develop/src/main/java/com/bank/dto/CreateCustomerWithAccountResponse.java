package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCustomerWithAccountResponse {

	private String customerName;
	private long customerId;
	private long accountNumber;
	private int pinNumber;
	
}

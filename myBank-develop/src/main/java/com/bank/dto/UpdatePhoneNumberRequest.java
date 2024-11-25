package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdatePhoneNumberRequest {

	private long accountNumber;
	private long phoneNumber;
	
}

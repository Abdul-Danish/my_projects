package com.bank.dto;

import com.bank.enums.Database;
import com.bank.model.Address;
import com.bank.model.DateOfBirth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateCustomerRequest {

	private String customerName;
	private long phoneNumber;
	private long adhaarNumber;
	private String email;
	private DateOfBirth dob;
	private Address address;
	private Database database;
	
}

package com.bank.dto;

import javax.validation.constraints.Size;

import com.bank.model.AccountType;
import com.bank.model.Address;
import com.bank.model.DateOfBirth;

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
public class CreateCustomerWithAccountRequest {
	
	@Size(min = 2, max = 30, message = "Invalid name, must be of size between 2-30")
	private String customerName;
	private long phoneNumber;
	private long adhaarNumber;
	private String email;
	private DateOfBirth dob;
	private Address address;
	
	private AccountType accountType;
	private long balance;
	
}

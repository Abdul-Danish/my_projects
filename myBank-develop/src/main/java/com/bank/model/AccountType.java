package com.bank.model;


public enum AccountType {
	SAVINGS("SAVINGS"), CURRENT("CURRENT"), SALARY("SALARY");
	
	private String type;
	
	private AccountType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}

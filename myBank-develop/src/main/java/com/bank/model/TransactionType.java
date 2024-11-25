package com.bank.model;

public enum TransactionType {
	WITHDRAW("WITHDRAW"), DEPOSIT("DEPOSIT");
	
	private String type;
	
	private TransactionType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}

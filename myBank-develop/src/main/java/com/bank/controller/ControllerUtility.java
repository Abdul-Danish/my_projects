package com.bank.controller;

import org.springframework.stereotype.Component;

import com.bank.model.DateOfBirth;

@Component
public class ControllerUtility {

	void checkForValidDayAndMonth(int day, int month) {
		
		if ((day <= 0) || (day > 31)) {
			throw new IllegalArgumentException("Invalid birth day");
		}
		if ((month <= 0) || (month > 12)) {
			throw new IllegalArgumentException("Invalid birth month");
		}
	}
	
	void checkForValidAgeAndYear(String currentDate, DateOfBirth dateOfBirth) {
		String[] listCurrentDate = currentDate.split("/");
		int currentYear = Integer.parseInt(listCurrentDate[0]);
	
		// checking for age eligibility
		int customerAge = currentYear - dateOfBirth.getYear();
		if (customerAge < 18) {
			throw new IllegalArgumentException("Invalid Age, Must be greater than or equal to 18");
		}
	
		// checking for valid year
		if (dateOfBirth.getYear() > currentYear || customerAge > 100) {
			throw new IllegalArgumentException("Invalid year");
		}
	}
	
	public void checkForValidPhoneNumber(String stringPhoneNumber) {
		if ((stringPhoneNumber.length() < 10) || (stringPhoneNumber.length() > 10)) {
		throw new IllegalArgumentException("Invalid Phone number");
		}
	}
	
	void checkForValidAdhaarLength(String stringAdhaarNumber) {
		if ((stringAdhaarNumber.length() < 12) || (stringAdhaarNumber.length() > 12)) {
			throw new IllegalArgumentException("Invalid Adhaar number");
		}
	}
	
}

package com.bank.service;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.InvalidAttributesException;
import javax.security.sasl.AuthenticationException;

import org.springframework.stereotype.Component;

import com.bank.model.Account;


@Component
public class Utility {

		// For invalid Account number
		void checkForInvalidAccountNumber(Account obj) throws InvalidAttributesException {
			if (obj == null) {
				throw new InvalidAttributesException("Invalid Account Number");
			}
		}
		
		// checking for valid Pin Number
		void checkForInvalidPinNumber(Account obj, int pinNumber) throws AuthenticationException {
			String stringPinNumber = String.valueOf(pinNumber);
			if ((stringPinNumber.length() < 4) || (stringPinNumber.length() > 4)) {
				throw new AuthenticationException("Invalid Pin Length");
			}
			else if (obj.getPinNumber() != pinNumber) {
				throw new AuthenticationException("Invalid Pin");
			}
		}
		
		// Checking if account is not Active
		void checkIfInActive(Account obj) throws InvalidAttributesException {
			if (!(obj.isActive())) {
				throw new InvalidAttributesException("Invalid Account");
			}
		}
		
		// checking for invalid amount
		void checkForInvalidAmount(long amount) {
			if (amount <= 0) {
				throw new IllegalArgumentException("Invalid Amount");
			}
		}
		
		// Checking for sufficient balance
		void checkForInsufficientBalance(Account obj, long amount) throws InvalidAttributesException {
			if (obj.getBalance() < amount) {
				throw new InvalidAttributesException("Insufficient Balance");
			}
		}
		
		// Checking if sender and receiver account numbers are same
		void checkForSameAccountNumber(long senderAccountNumber,long receiverAccountNumber) throws InstanceAlreadyExistsException {
			if (senderAccountNumber == receiverAccountNumber) {
				throw new IllegalArgumentException("Sender and Receiver account number should not be same");
			}
		}
		
		// checking for customerId length
		void checkForCustomerIdLength(long customerId) {
			String stringCustomerId = "" + customerId;
			if ((stringCustomerId.length() < 8) || (stringCustomerId.length() > 8)) {
				throw new IllegalArgumentException("Invalid Customer Id");
			}
		}
}

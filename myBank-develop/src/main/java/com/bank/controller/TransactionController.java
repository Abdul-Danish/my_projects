package com.bank.controller;

import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.InvalidAttributesException;
import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.DeleteTransactionRequest;
import com.bank.dto.DepositRequest;
import com.bank.dto.MiniStatementRequest;
import com.bank.dto.TransferRequest;
import com.bank.dto.WithdrawRequest;
import com.bank.model.Transaction;
import com.bank.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	@Autowired
	TransactionService transactionService;
 
	@PutMapping("/transfer")
	public Map<String, String> transfer(@RequestBody TransferRequest transferRequestObj) throws InvalidAttributesException, AuthenticationException, InstanceAlreadyExistsException {
		return transactionService.transferMoney(transferRequestObj);
	}
	
	@PutMapping("/deposit")
	public Map<String, String> deposit(@RequestBody DepositRequest depositRequestObj) throws InvalidAttributesException {
		return transactionService.depositMoney(depositRequestObj);
	}
	
	@PutMapping("/withdraw")
	public Map<String, String> withdraw(@RequestBody WithdrawRequest withdrawRequestObj) throws AuthenticationException, InvalidAttributesException {
		return transactionService.withdrawMoney(withdrawRequestObj);
	}
	
	@PostMapping("/mini-statement")
	public List<Transaction> miniStatement(@RequestBody MiniStatementRequest miniStatementRequestObj) throws AuthenticationException, InvalidAttributesException {
		return transactionService.getMiniStatement(miniStatementRequestObj);
	}
	
	@DeleteMapping
	public Map<String, String> removeTransaction(@RequestBody DeleteTransactionRequest deleteTransactionReq) {
		return transactionService.deleteTransaction(deleteTransactionReq);
	}
}

package com.bank.repository.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;


//@ConditionalOnProperty(value = "current.database", havingValue = "postgres")
@Repository
public interface TransactionRepositoryPostgres extends JpaRepository<Transaction, Long>, TransactionRepository {

}

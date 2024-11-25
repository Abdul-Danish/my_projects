package com.bank.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;


//@ConditionalOnProperty(value = "current.database", havingValue = "postgres")
@Repository
public interface AccountRepositoryPostgres extends JpaRepository<Account, Long>, AccountRepository {
	
}

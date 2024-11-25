package com.bank.repository.mongo;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;

//@ConditionalOnProperty(value = "current.database", havingValue = "mongo")
@Repository	
@JaversSpringDataAuditable
public interface TransactionRepositoryMongo extends MongoRepository<Transaction, Long>, TransactionRepository{

}

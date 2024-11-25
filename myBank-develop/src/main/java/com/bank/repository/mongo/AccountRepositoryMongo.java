package com.bank.repository.mongo;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;

//@ConditionalOnProperty(value = "current.database", havingValue = "mongo")
@Repository
@JaversSpringDataAuditable
public interface AccountRepositoryMongo extends MongoRepository<Account, Long>, AccountRepository {

}

package com.bank.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bank.enums.Database;
import com.bank.repository.AccountRepository;
import com.bank.repository.jpa.AccountRepositoryPostgres;
import com.bank.repository.mongo.AccountRepositoryMongo;

@Component
public class AccountRepositoryFactory {

    @Autowired
    private AccountRepositoryMongo accountRepositoryMongo;
    
    @Autowired
    private AccountRepositoryPostgres accountRepositoryPostgres;
    
    public AccountRepository getDatabase(Database database) {
        if (database.equals(Database.MONGO)) {
            return accountRepositoryMongo;
        } else if (database.equals(Database.POSTGRES)) {
            return accountRepositoryPostgres;
        }
        throw new IllegalArgumentException("Database Not Found!");
    }
    
}

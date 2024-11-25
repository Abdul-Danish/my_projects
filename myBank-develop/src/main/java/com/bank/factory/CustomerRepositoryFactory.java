package com.bank.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bank.enums.Database;
import com.bank.repository.CustomerRepository;
import com.bank.repository.jpa.CustomerRepositoryPostgres;
import com.bank.repository.mongo.CustomerRepositoryMongo;

@Component
public class CustomerRepositoryFactory {

    @Autowired
    private CustomerRepositoryMongo mongoRepository;

    @Autowired
    private CustomerRepositoryPostgres postgresRepository;

    public CustomerRepository getDatabase(Database database) {
        if (database.equals(Database.MONGO)) {
            return mongoRepository;
        } else if (database.equals(Database.POSTGRES)) {
            return postgresRepository;
        }
        throw new IllegalArgumentException("Database Not Found!");
    }

}

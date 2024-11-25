package com.bank.repository.mongo;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.bank.model.Address;
import com.bank.model.Customer;
import com.bank.repository.CustomerRepository;

//@ConditionalOnProperty(value = "current.database", havingValue = "mongo")
@Repository
@JaversSpringDataAuditable
public interface CustomerRepositoryMongo extends MongoRepository<Customer, Long>, CustomerRepository {

    @Override
    @Query(value = "{email: {$eq: ?0}}")
    public List<Customer> findByEmail(String email);
    
    @Override
    @Query(value = "{'address.city': {$eq: ?0}}")
    public List<Customer> findByCity(String city);
}

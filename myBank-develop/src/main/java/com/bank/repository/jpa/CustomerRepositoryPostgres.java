package com.bank.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bank.model.Customer;
import com.bank.repository.CustomerRepository;

//@ConditionalOnProperty(value = "current.database", havingValue = "postgres")
@Repository
public interface CustomerRepositoryPostgres extends JpaRepository<Customer, Long>, CustomerRepository {
 
    @Override
//    @Query(value = "select * from bank.customer where email NOT IN (:email)", nativeQuery = true)
    @Query(value = "SELECT * FROM bank.customer WHERE email = :email", nativeQuery = true)
    public List<Customer> findByEmail(String email);
    
    @Override
    @Query(value = "SELECT * FROM bank.customer WHERE address ->> 'city' = :city", nativeQuery = true)
    public List<Customer> findByCity(String city);
}

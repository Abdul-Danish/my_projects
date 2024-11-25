package com.batch.process.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.batch.process.model.Customer;

@Component
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}

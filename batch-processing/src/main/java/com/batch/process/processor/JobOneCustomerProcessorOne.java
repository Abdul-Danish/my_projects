package com.batch.process.processor;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.batch.process.model.Customer;

@Component
@StepScope
public class JobOneCustomerProcessorOne implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer customer) throws Exception { 
	    if (customer != null) {
	        String country = customer.getCountry();
	        customer.setCountry(country.concat("_FirstStep"));
	    }
		return customer;
	}
	
}

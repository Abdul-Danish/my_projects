package com.batch.process.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.batch.process.model.Customer;

@Component
public class JobThreeCustomerProcessorOne implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer customer) throws Exception {
		return customer;
	}

}

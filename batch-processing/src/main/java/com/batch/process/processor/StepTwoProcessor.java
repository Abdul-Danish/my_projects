package com.batch.process.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.batch.process.model.Customer;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StepTwoProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer customer) throws Exception {
		log.info("Executing Processor Two");
	    if (customer != null) {
	    	String batchStep = customer.getBatchStep();
	        customer.setBatchStep(batchStep.concat("_secondStep"));
	    }
		return customer;
	}

}


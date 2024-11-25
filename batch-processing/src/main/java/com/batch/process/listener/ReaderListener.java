package com.batch.process.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.batch.process.model.Customer;

@Component
public class ReaderListener implements ItemReader<Customer>, StepExecutionListener {

    private StepExecution stepExecution;
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Customer customer = new Customer();
        if (stepExecution!=null && (stepExecution.getJobParameters().getString("id")!=null)) {
            customer.setId(stepExecution.getJobParameters().getLong("id"));   
        }
        return customer;
    }

}

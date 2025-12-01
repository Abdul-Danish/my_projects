package com.batch.process.controller;

import java.util.UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.batch.process.model.Customer;
import com.batch.process.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/customer/")
@Slf4j
public class CustomerController {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	@Qualifier("batchJob")
	private Job batchJob;

	@PostMapping("process1")
	public void startJob1(@RequestBody Customer customer)
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
			JobParametersInvalidException, InterruptedException {
		Customer savedCustomer = customerRepository.save(customer);
		JobParameters jobParameters = new JobParametersBuilder().addLong("id", savedCustomer.getId())
				.addString("startAt1", UUID.randomUUID().toString()).toJobParameters();
		log.info("Job-1 Started");
		jobLauncher.run(batchJob, jobParameters);
	}

}

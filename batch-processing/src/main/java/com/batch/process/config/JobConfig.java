package com.batch.process.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.batch.process.listener.ReaderListener;
import com.batch.process.model.Customer;
import com.batch.process.processor.StepOneProcessor;
import com.batch.process.processor.StepTwoProcessor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing
@Slf4j
public class JobConfig {

	@Autowired
	private ReaderListener readerListener;

	@Bean("batchJob")
	public Job batchJob(JobRepository jobRepository, @Qualifier("jobStep1") Step step1,
			@Qualifier("jobStep2") Step step2) {
		return new JobBuilderFactory(jobRepository).get("job1").incrementer(new RunIdIncrementer()).flow(step1)
				.next(step2).end().build();
	}

	@Bean("jobStep1")
	public Step job1step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			@Qualifier("reader1") ItemReader<Customer> itemReader, StepOneProcessor itemProcessor,
			@Qualifier("writer") ItemWriter<Customer> itemWriter) throws InterruptedException {
		System.out.println("Running step1");
		return new StepBuilderFactory(jobRepository, transactionManager).get("step1").<Customer, Customer>chunk(1)
				.reader(itemReader).processor(itemProcessor).writer(itemWriter).listener(readerListener).build();
	}

	@Bean("jobStep2")
	public Step job1step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			@Qualifier("reader2") ItemReader<Customer> itemReader, StepTwoProcessor itemProcessor,
			@Qualifier("writer") ItemWriter<Customer> itemWriter) {
		System.out.println("Running step2");
		return new StepBuilderFactory(jobRepository, transactionManager).get("step2")
				.transactionManager(transactionManager).<Customer, Customer>chunk(1).reader(itemReader)
				.processor(itemProcessor).writer(itemWriter).listener(readerListener).build();
	}

	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(10);
		taskExecutor.setThreadGroupName("writer task");
		return taskExecutor;
	}

//	@Bean
//	public JobRepository createJobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
//	    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
//	    factory.setDataSource(dataSource);
//	    factory.setTransactionManager(transactionManager);
//	    factory.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
//	    return factory.getObject();
//	}
//	
//	@Bean
//	public TransactionProxyFactoryBean baseProxy(JobRepository  jobRepository, PlatformTransactionManager transactionManager ) {
//		TransactionProxyFactoryBean transactionProxyFactoryBean = new TransactionProxyFactoryBean();
//		Properties transactionAttributes = new Properties();
//		transactionAttributes.setProperty("*", "PROPAGATION_REQUIRED");
//		transactionProxyFactoryBean.setTransactionAttributes(transactionAttributes);
//		transactionProxyFactoryBean.setTarget(jobRepository);
//		transactionProxyFactoryBean.setTransactionManager(transactionManager);
//		return transactionProxyFactoryBean;
//	}

}

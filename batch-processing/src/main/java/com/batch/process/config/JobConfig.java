package com.batch.process.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.springframework.web.context.annotation.RequestScope;

import com.batch.process.listener.ReaderListener;
import com.batch.process.model.Customer;
import com.batch.process.processor.JobOneCustomerProcessorOne;
import com.batch.process.processor.JobOneCustomerProcessorTwo;
import com.batch.process.processor.JobTwoCustomerProcessorOne;
import com.batch.process.processor.JobTwoCustomerProcessorTwo;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing
@Slf4j
public class JobConfig {

    @Autowired
    private ReaderListener readerListener;

    @Bean
//	("job1")
    public Job runJob1(JobRepository jobRepository, @Qualifier("job1step1") Step step1, @Qualifier("job1step2") Step step2) {
        return new JobBuilderFactory(jobRepository).get("job1").incrementer(new RunIdIncrementer()).flow(step1).next(step2).end().build();
    }

//	@Bean("job2")
//	@RequestScope
    public Job runJob2(JobRepository jobRepository, @Qualifier("job2step1") Step step1

//			,@Qualifier("job2step2") Step step2
    ) {
        System.out.println(step1);
        return new JobBuilderFactory(jobRepository).get("job2").flow(step1)
//				.next(step2)
            .end().build();
    }

//	@Bean("job3")
    public Job runJob3(JobRepository jobRepository, @Qualifier("job3step1") Step step1
//			,@Qualifier("job3step2") Step step2
    ) {
        return new JobBuilderFactory(jobRepository).get("job3").flow(step1)
//				.next(step2)
            .end().build();
    }
    
    @Bean("job1step1")
    public Step job1step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        @Qualifier("job1reader1") ItemReader<Customer> itemReader, JobOneCustomerProcessorOne itemProcessor,
        @Qualifier("writer") ItemWriter<Customer> itemWriter) throws InterruptedException {
        return new StepBuilderFactory(jobRepository, transactionManager).get("job1-step1").<Customer, Customer>chunk(10).reader(itemReader)
            .processor(itemProcessor).writer(itemWriter).listener(readerListener).taskExecutor(taskExecutor()).build();
    }

    @Bean("job1step2")
    public Step job1step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        @Qualifier("job1reader2") ItemReader<Customer> itemReader, JobOneCustomerProcessorTwo itemProcessor,
        @Qualifier("writer") ItemWriter<Customer> itemWriter) {
        System.out.println("Running job1-step2");
        return new StepBuilderFactory(jobRepository, transactionManager).get("job1-parse-second-csv").transactionManager(transactionManager)
            .<Customer, Customer>chunk(1).reader(itemReader).processor(itemProcessor).writer(itemWriter).listener(readerListener)
//				.taskExecutor(taskExecutor())
            .build();
    }

//	@Bean("job2step1")
    public Step job2step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<Customer> itemReader,
        JobTwoCustomerProcessorOne itemProcessor, ItemWriter<Customer> itemWriter) throws InterruptedException {
        return new StepBuilderFactory(jobRepository, transactionManager).get("job2-step1").<Customer, Customer>chunk(10).reader(itemReader)
            .processor(itemProcessor).writer(itemWriter).build();
    }

//	@Bean("job2step2")
    public Step job2step2(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<Customer> itemReader,
        JobTwoCustomerProcessorTwo itemProcessor, ItemWriter<Customer> itemWriter) {
        return new StepBuilderFactory(jobRepository, transactionManager).get("job2-parse-second-csv").<Customer, Customer>chunk(10)
            .reader(itemReader).processor(itemProcessor).writer(itemWriter).build();
    }

//	@Bean("job3step1")
    public Step job3step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<Customer> itemReader,
        JobTwoCustomerProcessorOne itemProcessor, ItemWriter<Customer> itemWriter) throws InterruptedException {
        return new StepBuilderFactory(jobRepository, transactionManager).get("job3-step1").<Customer, Customer>chunk(10).reader(itemReader)
            .processor(itemProcessor).writer(itemWriter).build();
    }

//	@Bean("job3step2")
    public Step job3step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
        @Qualifier("job3reader2") ItemReader<Customer> itemReader, JobTwoCustomerProcessorTwo itemProcessor,
        @Qualifier("job3writer2") ItemWriter<Customer> itemWriter) {
        return new StepBuilderFactory(jobRepository, transactionManager).get("job3-parse-second-csv").<Customer, Customer>chunk(10)
            .reader(itemReader).processor(itemProcessor).writer(itemWriter).build();
    }

    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        taskExecutor.setThreadGroupName("writer task");
        return taskExecutor;
    }

//	@Bean("step1")
//	public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager, 
//			FlatFileItemReader<Customer> itemReader, CustomerProcessor itemProcessor, 
//			RepositoryItemWriter<Customer> itemWriter) {
//		return new StepBuilder("step1", jobRepository).<Customer, Customer>chunk(10, transactionManager)
//				.reader(itemReader)
//				.processor(itemProcessor)
//				.writer(itemWriter)
//				.build();
//	}

//	@Bean("job")
//	public Job runJob(JobRepository jobRepository, @Qualifier("step1") Step step1, JobCompletionNotificationListener listener) {
//		return new JobBuilder("step-1", jobRepository)
//				.listener(listener)
//				.start(step1)
//				.build();
//	}

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

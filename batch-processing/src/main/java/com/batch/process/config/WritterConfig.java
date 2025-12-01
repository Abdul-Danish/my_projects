package com.batch.process.config;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.batch.process.model.Customer;
import com.batch.process.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WritterConfig {

	@Autowired
	private CustomerRepository customerRepository;

	@Bean("writer")
	@StepScope
	public RepositoryItemWriter<Customer> job1writer1() {
		log.info("Executing writer");
		RepositoryItemWriter<Customer> itemWriter = new RepositoryItemWriter<>();
		itemWriter.setRepository(customerRepository);
		itemWriter.setMethodName("save");
		return itemWriter;
	}

//	@Bean("writer")
//	JdbcBatchItemWriter<Customer> getJdbcBatchItemWriter(DataSource dataSource) {
//	    String query = "UPDATE batch.customer set country='Updated' where country='Initial' AND id=3063";
//        return new JdbcBatchItemWriterBuilder<Customer>()
//            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>()).sql(query).dataSource(dataSource).build();
//    }

}

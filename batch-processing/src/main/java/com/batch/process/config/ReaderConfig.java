package com.batch.process.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.batch.process.listener.ReaderListener;
import com.batch.process.model.Customer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ReaderConfig {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ReaderListener readerListener;

	@Bean("reader1")
	@StepScope
	public JdbcCursorItemReader<Customer> job1reader1() throws Exception {
		String query = "select * from BATCH_PROCESS.CUSTOMER WHERE id='%s' AND batch_step='%s'";
		String id = "0";
		try {
			if (readerListener != null && readerListener.read().getId() != 0) {
				id = String.valueOf(readerListener.read().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fetchQuery = String.format(query, id, "Initial");
		log.info("Step-1 Reader Id: {}, query: {}", id, fetchQuery);
		JdbcCursorItemReader<Customer> databaseReader = new JdbcCursorItemReader<>();
		databaseReader.setCurrentItemCount(1);
		databaseReader.setSaveState(false);
		databaseReader.setVerifyCursorPosition(false);

		databaseReader.setDataSource(dataSource);
		databaseReader.setSql(fetchQuery);
		databaseReader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));
		databaseReader.afterPropertiesSet();
		return databaseReader;
	}

	@Bean("reader2")
	@StepScope
	public JdbcCursorItemReader<Customer> job1reader2() throws Exception {
		String query = "select * from BATCH_PROCESS.CUSTOMER WHERE id='%s' AND batch_step='%s'";
		String id = "0";
		try {
			if (readerListener != null && readerListener.read().getId() != 0) {
				id = String.valueOf(readerListener.read().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fetchQuery = String.format(query, id, "Initial_firstStep");
		log.info("Step-2 Reader Id: {}, query: {}", id, fetchQuery);
		JdbcCursorItemReader<Customer> databaseReader = new JdbcCursorItemReader<>();
		databaseReader.setCurrentItemCount(1);
		databaseReader.setSaveState(false);
		databaseReader.setVerifyCursorPosition(false);

		databaseReader.setDataSource(dataSource);
		databaseReader.setSql(fetchQuery);
		databaseReader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));
		databaseReader.afterPropertiesSet();
		return databaseReader;
	}

//    @Bean("job1reader1")
//    @StepScope
//    public FlatFileItemReader<Customer> job1reader1() {
//        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//        itemReader.setResource(new FileSystemResource("src/main/resources/job1Resources/customers.csv"));
//        itemReader.setName("csv");
//        itemReader.setLinesToSkip(1);
//        itemReader.setLineMapper(LineMapper());
//        return itemReader;
//    }

	private LineMapper<Customer> LineMapper() {

		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

}

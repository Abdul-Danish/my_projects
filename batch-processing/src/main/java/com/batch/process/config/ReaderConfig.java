package com.batch.process.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.batch.process.listener.ReaderListener;
import com.batch.process.model.Customer;
import com.batch.process.repository.CustomerRepository;

@Configuration
public class ReaderConfig {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private ReaderListener readerListener;
    
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

    /*
     * Temp
     */        
    
    @Bean
    ("job1reader1")
    @StepScope
    public JdbcCursorItemReader<Customer> job1reader1() {
        String query = "select * from BATCH.CUSTOMER WHERE id='%s' AND country='%s'";
        String id = "0";
        try {
            if (readerListener != null && readerListener.read().getId() != 0) {
                id = String.valueOf(readerListener.read().getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Reader Id: " + id);
        JdbcCursorItemReader<Customer> databaseReader = new JdbcCursorItemReader<>();
        databaseReader.setCurrentItemCount(1);
        databaseReader.setSaveState(false);
        databaseReader.setVerifyCursorPosition(false);
        
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(String.format(query, id, "Initial"));
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));
        return databaseReader;
    }

    
    @Bean
    ("job1reader2")
    @StepScope
    public JdbcCursorItemReader<Customer> job1reader2() {
//        Customer savedCustomer = customerRepository.save(Customer.builder().firstName("test").lastName("user").country("Initial").build());
        
        String query = "select * from BATCH.CUSTOMER WHERE id='%s' AND country='%s'";
        String id = "0";
        try {
            if (readerListener != null && readerListener.read().getId() != 0) {
                id = String.valueOf(readerListener.read().getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Reader Id: " + id);
        JdbcCursorItemReader<Customer> databaseReader = new JdbcCursorItemReader<>();
        databaseReader.setCurrentItemCount(1);
        databaseReader.setSaveState(false);
        databaseReader.setVerifyCursorPosition(false);
        
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(String.format(query, id, "Initial_FirstStep"));
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));
        return databaseReader;
    }
    
    /*
     * END
     */
    

//	@Bean("job1reader2")
//	public FlatFileItemReader<Customer> job1reader2() {
//		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//		itemReader.setResource(new FileSystemResource("src/main/resources/job1Resources/customers_4.csv"));
//		itemReader.setName("csv");
//		itemReader.setLinesToSkip(1);
//		itemReader.setLineMapper(LineMapper());
//		return itemReader;
//	}
//	
//	
//	@Bean("job2reader1")
//	public FlatFileItemReader<Customer> job2reader1() {
//		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//		itemReader.setResource(new FileSystemResource("src/main/resources/job2Resources/customers_5.csv"));
//		itemReader.setName("csv");
//		itemReader.setLinesToSkip(1);
//		itemReader.setLineMapper(LineMapper());
//		return itemReader;
//	}
//	
//	@Bean("job2reader2")
//	public FlatFileItemReader<Customer> job2reader2() {
//		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//		itemReader.setResource(new FileSystemResource("src/main/resources/job2Resources/customers_6.csv"));
//		itemReader.setName("csv");
//		itemReader.setLinesToSkip(1);
//		itemReader.setLineMapper(LineMapper());
//		return itemReader;
//	}
//	
//	@Bean("job3reader1")
//	public FlatFileItemReader<Customer> job3reader1() {
//		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//		itemReader.setResource(new FileSystemResource("src/main/resources/job3Resources/customers_7.csv"));
//		itemReader.setName("csv");
//		itemReader.setLinesToSkip(1);
//		itemReader.setLineMapper(LineMapper());
//		return itemReader;
//	}
//	
//	@Bean("job3reader2")
//	public FlatFileItemReader<Customer> job3reader2() {
//		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//		itemReader.setResource(new FileSystemResource("src/main/resources/job3Resources/customers_8.csv"));
//		itemReader.setName("csv");
//		itemReader.setLinesToSkip(1);
//		itemReader.setLineMapper(LineMapper());
//		return itemReader;
//	}

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

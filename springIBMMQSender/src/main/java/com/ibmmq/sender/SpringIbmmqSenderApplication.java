package com.ibmmq.sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class SpringIbmmqSenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringIbmmqSenderApplication.class, args);
	}

}

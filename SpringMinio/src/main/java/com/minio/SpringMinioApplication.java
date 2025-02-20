package com.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringMinioApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMinioApplication.class, args);
	}

	@Bean
	RestTemplate restTemplateBean() {
	    return new RestTemplate();
	}
	
}

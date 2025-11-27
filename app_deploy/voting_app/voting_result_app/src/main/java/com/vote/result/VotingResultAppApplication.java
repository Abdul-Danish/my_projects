package com.vote.result;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan({"com.vote"})
@EnableMongoRepositories(basePackages = {"com.vote.lib.repository"})
public class VotingResultAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingResultAppApplication.class, args);
	}

}

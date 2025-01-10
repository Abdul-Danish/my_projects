package com.digitaldots.function;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class SampleFunctionApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SampleFunctionApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        log.debug("log...");
    }

}

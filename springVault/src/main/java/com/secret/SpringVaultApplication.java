package com.secret;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.secret.model.Credentials;

@SpringBootApplication
@EnableConfigurationProperties(Credentials.class)
public class SpringVaultApplication implements  CommandLineRunner {

    @Autowired
    private Credentials credentials;
    
	public static void main(String[] args) {
		SpringApplication.run(SpringVaultApplication.class, args);
	}

	@Override
    public void run(String... args) throws Exception {
	    System.out.println("------------properties---------");
	    System.out.println("Username : "+credentials.getUsername());
	    System.out.println("Password : "+credentials.getPassword());
    }
	
}

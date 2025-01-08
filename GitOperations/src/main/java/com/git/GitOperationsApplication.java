package com.git;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.git.service.GitService;

@SpringBootApplication
public class GitOperationsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GitOperationsApplication.class, args);
	}
	
	@Autowired
	private GitService gitService;

    @Override
    public void run(String... args) throws Exception {
        gitService.createBranch();
    }

}

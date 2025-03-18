package com.aop;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aop.model.Sample;
import com.aop.service.SampleService;

@SpringBootApplication
public class SpringAopApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringAopApplication.class, args);
	}

	@Autowired
	private SampleService sampleService;
	
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Exec...");
        sampleService.execute(Sample.builder().id("100").title("NA").build());
    }
    
    @RestController
    class TestController {
        
        @PostMapping("/api/exec")
        public ResponseEntity<Sample> execute() {
            return ResponseEntity.ok(sampleService.execute(Sample.builder().id("100").title("NA").build()));
        }
    }

}

package com.batch.process;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
//@EnableJpaRepositories("com.batch.process.repository")
//@EntityScan("com.batch.process.model")
//@ComponentScan(basePackages = {"com.batch.process"})
public class BatchProcessingApplication implements CommandLineRunner {
	
	public static void main(String[] args) {
		SpringApplication.run(BatchProcessingApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
//        Object data = getData();
//        System.out.println(data);
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Map<String, String> extractUriTemplateVariables = antPathMatcher.extractUriTemplateVariables("/api/v1/solutions/{solutionId}/**", "/api/v1/solutions/exports");
        System.out.println(extractUriTemplateVariables.get("solutionId"));
    }
	
    public static Object getData() throws JsonProcessingException, IllegalArgumentException {
        Map obj = new HashMap<>();
        obj.put("data", 1);
        obj.put("message", "ACCEPTED");
        obj.put("status", 200);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        objectMapper.findAndRegisterModules();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        JSONObject response = objectMapper.convertValue(objectMapper.writeValueAsString(obj), JSONObject.class);
        System.out.println("Res " + response);
        return Objects.nonNull(response.get("data")) ? response.get("data") : response;
    }
    
}

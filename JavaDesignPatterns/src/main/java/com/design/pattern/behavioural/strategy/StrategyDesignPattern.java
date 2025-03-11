package com.design.pattern.behavioural.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/*
 * It is possible to select an object's behavior at runtime by utilizing the Strategy Design Pattern.
 * 
 * It is possible to select an object's behavior at runtime by utilizing the Strategy Design Pattern.
 * 
 * Similar To: Factory Design Pattern
 */

@SpringBootApplication
@Slf4j
public class StrategyDesignPattern {

    @Autowired
    private List<HttpAuthenticationType> authenticationType;

    private static Map<String, HttpAuthenticationType> authMap = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("Inside Init");
        authenticationType.forEach(authType -> authMap.put(authType.getType().name(), authType));
    }

    public static void main(String[] args) {
        SpringApplication.run(StrategyDesignPattern.class, args);
        log.info("auth map {}", authMap);
        HttpAuthenticationType httpAuthenticationType = authMap.get(AuthType.OAUTH_CODE_GRANT.name());
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("user", "test@gmail.com");
        httpAuthenticationType.authenticate(credentials);
    }
}

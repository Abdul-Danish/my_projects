package com.secret.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.secret.model.Credentials;
import com.secret.service.SecretsService;

@RestController
//@RequestMapping("/api/v1/secret")
public class SecretsController {

    @Autowired
    private SecretsService secretsService;
    
    @Autowired
    private Credentials credentials;
    
    @GetMapping("/test")
    public String test() {
        String userName2 = credentials.getUsername();
        String password = credentials.getPassword();
        
        System.out.println("Name: " + userName2);
        System.out.println("Pass: " + password);
        return userName2;
    }
    
//    @PostMapping("/api/v1/secret")
//    public String createSecret(@RequestBody Credentials credentials) {
//        return secretsService.encryptMessage(credentials);
//    }
//    
    @GetMapping
    public Credentials getSecret(@RequestBody String userName) {
        return secretsService.findById(userName).get();
    }
    
}

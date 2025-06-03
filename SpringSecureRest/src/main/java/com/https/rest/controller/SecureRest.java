package com.https.rest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.https.rest.service.RestService;

@RestController
@RequestMapping("/api/v1/secure")
public class SecureRest {

    @Autowired
    private RestService restService;
    
    
    // https://localhost:8000/api/v1/secure/data
    @GetMapping("/data")
    public ResponseEntity<Map<String, String>> getSecureData() {
        return ResponseEntity.ok(restService.getSecureData());
    }
    
}

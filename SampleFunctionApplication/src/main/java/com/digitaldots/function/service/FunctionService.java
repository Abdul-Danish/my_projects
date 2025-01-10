package com.digitaldots.function.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.digitaldots.connector.handler.Handler;

@Component
public class FunctionService implements Handler {

    @Override
    public Map<String, Object> handle(Map<String, Map<String, Object>> request, Map<String, Map<String, String>> headers) {
        System.out.println("Request processing");
        System.out.println("Request: " + request.get("request"));
        System.out.println("Request: " + request.get("request").get("content"));
        Map<String, Object> response = new HashMap<>();
        response.put("response", request.get("request").get("content"));
        return response;
    }
    
}

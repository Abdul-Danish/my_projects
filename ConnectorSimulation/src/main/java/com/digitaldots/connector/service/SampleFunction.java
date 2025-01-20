package com.digitaldots.connector.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.digitaldots.connector.handler.SampleConnectorRequest;

@Component
public class SampleFunction implements ConnectorHandler {

    @Override
    public Map<String, Object> execute(SampleConnectorRequest request) {  
        Map<String, Object> requestParameters = request.getRequestParameters();
        System.out.println("CONTENT IS: " + requestParameters.get("content"));
        Map<String, Object> response = new HashMap<>();
        response.put("response", "executed successfully");
        return response;
    }

}

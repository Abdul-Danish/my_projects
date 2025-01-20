package com.digitaldots.connector.service;

import java.util.Map;

import com.digitaldots.connector.handler.SampleConnectorRequest;

public interface ConnectorHandler {

    Map<String, Object> execute(SampleConnectorRequest headers);
    
}

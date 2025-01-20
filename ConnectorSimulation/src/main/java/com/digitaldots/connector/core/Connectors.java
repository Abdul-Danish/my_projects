package com.digitaldots.connector.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class Connectors<C extends Connector<? extends ConnectorRequest<? extends ConnectorResponse>>> {

    Map<String, C> availableConnectors = new ConcurrentHashMap<>();

    @Autowired
    protected List<C> connectorsList;

//    public Connectors(C connectorInstance) {
//        this.connectorInstance = connectorInstance;
//    }

    @PostConstruct
    public void initialize() {
        Map<String, C> connectors = new HashMap<>();
        for (C connector : connectorsList) {
            connectors.put(connector.getId(), connector);
        }
        availableConnectors = connectors;
    }
    
    public C getConnectorByType(String type) {
        return availableConnectors.get(type);
    }
}

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.spi.Connector;
import com.digitaldots.connector.spi.ConnectorProvider;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.connector.spi.ConnectorResponse;

/**
 * Provides access to all available connectors.
 */
@Component
public class Connectors<C extends Connector<? extends ConnectorRequest<? extends ConnectorResponse>>> {

    protected Map<String, C> availableConnectors = new ConcurrentHashMap<>();
    @Autowired
    private List<C> connectorsList;

    /**
     * Load all available connectors.
     */
    @PostConstruct
    public void loadConnectors() {
        this.initializeConnectors();
    }

    /**
     * Register a new connector.
     */
    protected void registerConnector(C connector) {
        registerConnector(connector.getId(), connector);
    }

    /**
     * Register a new connector under the given connector id.
     */
    protected void registerConnector(String connectorId, C connector) {
        this.registerConnectorInstance(connectorId, connector);
    }

    protected void unregisterConnector(String connectorId) {
        this.unregisterConnectorInstance(connectorId);
    }

    // instance //////////////////////////////////////////////////////////

    /**
     * @return all register connectors
     */
    public Set<C> getAllAvailableConnectors() {
        return new HashSet<C>(availableConnectors.values());
    }

    protected void initializeConnectors() {
        Map<String, C> connectors = new HashMap<>();
        for (C connector : connectorsList) {
            connectors.put(connector.getId(), connector);
        }
        this.availableConnectors = connectors;
    }

    protected void registerProvider(Map<String, C> connectors, ConnectorProvider<C> provider) {
        String connectorId = provider.getConnectorId();
        C connectorInstance = provider.createConnectorInstance();
        connectors.put(connectorId, connectorInstance);
    }

    public void registerConnectorInstance(String connectorId, C connector) {
        synchronized (Connectors.class) {
            availableConnectors.put(connectorId, connector);
        }
    }

    protected void unregisterConnectorInstance(String connectorId) {
        synchronized (Connectors.class) {
            availableConnectors.remove(connectorId);
        }
    }

    /**
     * @return the connector for the given id or null if no connector is registered for this id
     */
    public C getConnectorById(String connectorId) {
        return availableConnectors.getOrDefault(connectorId, availableConnectors.get("Generic"));
    }

}

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StoreFactory {

    protected static final Map<String, Store> connectorStorage = new HashMap<>();

    @Autowired
    private List<Store> connectorStores;

    @PostConstruct
    void init() {
        for (Store connectorStore : connectorStores) {
            connectorStorage.put(connectorStore.getDBTye(), connectorStore);
        }
    }

    public Store getDBDataSource(String dbType) {
        return connectorStorage.get(dbType);
    }
}

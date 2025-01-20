/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.mongo;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MongoHelper implements Serializable {
    private static final long serialVersionUID = -1;
    private MongoClient mongoClient;
    private Map<String, Object> configProperties;
    private String defaultDatabase;

    public MongoDatabase getDatabase(String database) {
        if (Objects.nonNull(database)) {
            return mongoClient.getDatabase(database);
        }
        return mongoClient.getDatabase(defaultDatabase);
    }

}

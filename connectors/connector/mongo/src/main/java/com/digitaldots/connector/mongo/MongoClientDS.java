/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.mongo;

import static com.mongodb.AuthenticationMechanism.PLAIN;
import static com.mongodb.AuthenticationMechanism.SCRAM_SHA_1;
import static com.mongodb.AuthenticationMechanism.SCRAM_SHA_256;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.cache.Store;
import com.mongodb.AuthenticationMechanism;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCursor;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MongoClientDS implements Store<MongoHelper, MongoHelper> {

    private static final int MAX_LIFETIME = 600;
    private static final int IDLE_TIMEOUT = 300;
    private static final int CONNECTION_TIMEOUT = 30;
    private static final int POOL_SIZE = 25;

    @Override
    public MongoHelper getDataSource(Map<String, Object> configProperties) {
        try {
            Assert.hasLength((String) configProperties.get(MongoConstants.URL), "Connection URL Should not be Empty");
            MongoClientOptions.Builder builder = MongoClientOptions.builder()
                .connectionsPerHost(Integer.parseInt("" + configProperties.getOrDefault("poolSize", POOL_SIZE)))
                .connectTimeout(Integer.parseInt("" + configProperties.getOrDefault("connectionTimeout", CONNECTION_TIMEOUT)))
                .maxConnectionIdleTime(Integer.parseInt("" + configProperties.getOrDefault("idleTimeout", IDLE_TIMEOUT)))
                .maxConnectionLifeTime(Integer.parseInt("" + configProperties.getOrDefault("maxLifeTime", MAX_LIFETIME)))
                .connectTimeout(Integer.parseInt("" + configProperties.getOrDefault("maxLifeTime", MAX_LIFETIME)));
            UriBuilder mongoURI = UriComponentsBuilder.fromUriString((String) configProperties.get(MongoConstants.URL));
            String replicationSet = (String) configProperties.get(MongoConstants.REPLICATION_SET_NAME);
            if (Objects.nonNull(replicationSet)) {
                mongoURI.queryParam(MongoConstants.REPLICATION_SET_NAME, replicationSet);
            }
            Map<String, String> connectionProperties = (LinkedHashMap<String, String>) configProperties
                .getOrDefault(MongoConstants.CONNECTION_PROPERTIES, new LinkedHashMap<>());
            MultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();
            connectionProperties.forEach(linkedMultiValueMap::add);
            mongoURI.queryParams(linkedMultiValueMap);
            MongoClient client = getClient(configProperties, mongoURI, builder);
            log.trace("Mongo client build Successfully");
            MongoHelper mongoHelper = MongoHelper.builder().mongoClient(client).configProperties(configProperties)
                .defaultDatabase((String) configProperties.get("defaultDatabase")).build();
            return mongoHelper;
        } catch (Exception e) {
            log.error("Exception Occured while creating mongo datasource", e);
            throw new ConnectorException("Exception Occured while creating mongo client" + e.getMessage());
        }
    }

    private MongoClient getClient(Map<String, Object> configProperties, UriBuilder mongoURI, MongoClientOptions.Builder builder)
        throws UnsupportedEncodingException {
        AuthenticationMechanism authMechanism = AuthenticationMechanism
            .fromMechanismName((String) configProperties.getOrDefault("authMethod", "SCRAM-SHA-1"));
        String mechanismName = authMechanism.getMechanismName();
        if (Boolean.TRUE.equals(configProperties.get(MongoConstants.USE_AUTH)) && configProperties.containsValue("vaultCert")) {
            log.trace("SSL is Enabled for MongoDb");
            builder.sslEnabled(true).sslInvalidHostNameAllowed(true).sslContext((SSLContext) configProperties.get("sslContext"));
        }
        if (!mongoURI.build().getScheme().contains("srv")) {
            Assert.hasLength((String) configProperties.get(MongoConstants.AUTH_DATABASE), "AuthDatabase Should not be Empty");
            if (mongoURI.build().toString().contains(MongoConstants.AUTH_SOURCE)) {
                mongoURI.replaceQueryParam(MongoConstants.AUTH_SOURCE, configProperties.get("authDatabase"));
            } else {
                mongoURI.queryParam(MongoConstants.AUTH_SOURCE, configProperties.get("authDatabase"));
            }
        }
        String defaultDb = (String) configProperties.get("defaultDatabase");
        if (StringUtils.hasLength(defaultDb)) {
            mongoURI.path(defaultDb);
        } else {
            mongoURI.path("/");
        }
        mongoURI.queryParam(MongoConstants.AUTH_MECHANISM, mechanismName);
        log.trace("Connecting mongo with {} mechanism", mechanismName);
        attachUserInfo(mongoURI, authMechanism, configProperties);
        MongoClientURI clientUri = new MongoClientURI(mongoURI.build().toString(), builder);
        return new MongoClient(clientUri);
    }

    private void attachUserInfo(UriBuilder mongoURI, AuthenticationMechanism authMechanism, Map<String, Object> configProperties)
        throws UnsupportedEncodingException {
        if (authMechanism == PLAIN || authMechanism == SCRAM_SHA_1 || authMechanism == SCRAM_SHA_256) {
            Assert.hasLength((String) configProperties.get(MongoConstants.AUTH_USERNAME), "UserName Should not be Empty");
            Assert.hasLength((String) configProperties.get(MongoConstants.AUTH_PASSWORD), "Password Should not be Empty");
            log.trace("Mongo connection using username and password");
            mongoURI.userInfo(configProperties.get("authUserName") + ":" + configProperties.get("authPassword"));
        }
    }

    @Override
    public String getDBTye() {
        return "MongoDB";
    }

    @Override
    public MongoHelper getConnection(MongoHelper datasource) {
        return datasource;
    }

    @Override
    public String validate(MongoHelper mongoHelper, Map<String, Object> properties) {
        try {
            MongoCursor<Document> cursor = mongoHelper.getMongoClient().listDatabases().iterator();
            cursor.close();
            log.trace("Mongoconnection is validated");
            return "Successfully Connected";
        } catch (Exception e) {
            log.error("Exception Occured during mongo connection {}", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.digitaldots.connector.cache.Store;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FunctionDS implements Store<Function, Function> {

    @Value("${function.gateway.url}")
    private String gatewayConnectionUrl;

    @Value("${functionservice.username}")
    private String functionsDeployUserName;

    @Value("${functionservice.password}")
    private String functionsDeployPassword;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Function getConnection(Function datasource) {
        return datasource;
    }

    @Override
    public String getDBTye() {
        return "SCRIPT"; // ProcessConstants.SCRIPT_TASK;
    }

    @Override
    public Function getDataSource(Map<String, Object> configProperties) {
        return Function.builder().solutionId((String) configProperties.get("solutionId"))
            .alternateId((String) configProperties.get("alternateId")).version((String) configProperties.get("version"))
            .gateway(this.gatewayConnectionUrl).build();
    }

    @Override
    public Object validate(Function dataSource, Map<String, Object> parameters) {
        log.debug("DataSource to validate {}", dataSource);
        ObjectMapper mapper = new ObjectMapper();
        String functionServiceRunUrl = dataSource.generateGateWayUrl();
        try {
            URI uri = new URI(functionServiceRunUrl);
            RequestEntity<?> requestEntity = RequestEntity.post(uri)
                .headers(createHeaders(functionsDeployUserName, functionsDeployPassword)).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(parameters.get("payload")));
            log.debug("requestentity :: {}", requestEntity);
            return restTemplate.exchange(requestEntity, String.class).getBody();
        } catch (RestClientException | JsonProcessingException | URISyntaxException e) {
            log.error("unable to parse request body {} ", e);
            throw new IllegalArgumentException(e);
        }
    }

    private HttpHeaders createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.add("Authorization", authHeader);
        return headers;
    }
}

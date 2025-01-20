/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script.service;

import java.net.URI;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Connector(id = "SCRIPT")
@Slf4j
@Getter
public class ScriptService {

    @Params()
    protected String payload;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Connection
    protected Function functionApi;

    @Value("${function.gateway.url}")
    private String gatewayConnectionUrl;

    @Execute
    public JSONObject execute(ConnectorRequest<?> request) {
        String response = "";
        try {
            log.debug("payload is {} ", payload);
            URI uri = new URI(functionApi.generateGateWayUrl());
            log.debug("invoking faas using uri {} ", uri);
            RequestEntity<String> requestEntity = RequestEntity.method(HttpMethod.POST, uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).body(payload);
            response = restTemplate.exchange(requestEntity, String.class).getBody();
            log.debug("Response from function is {} ", response);
            return mapper.convertValue(response, JSONObject.class);
        } catch (Exception e) {
            log.error("Exception occured while executing script service", e);
            throw new ConnectorException("Exception occured while executing script service", e);
        }
    }

}

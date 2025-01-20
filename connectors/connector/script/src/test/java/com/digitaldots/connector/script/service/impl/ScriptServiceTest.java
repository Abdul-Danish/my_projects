/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.digitaldots.connector.script.service.ScriptService;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.connector.spi.ConnectorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScriptServiceTest {

    private ScriptService scriptService;

    @Before
    public void setup() {
        scriptService = new ScriptService();

    }

    @Test
    public void testExecute() {
        ConnectorRequest connectionRequest = new CustomConnectorRequest();
        connectionRequest.setRequestParameter("connector:connectorId", "fa1b6h2i6l7a0s1h332005-1-0");
        System.out.println(connectionRequest.getRequestParameters());
        JSONObject response = scriptService.execute(connectionRequest);
        log.info(response.toString());
    }

    class CustomConnectorRequest implements ConnectorRequest<ConnectorResponse> {
        Map<String, Object> request = new HashMap<>();

        @Override
        public void setRequestParameters(Map<String, Object> params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setRequestParameter(String name, Object value) {
            request.put(name, value);

        }

        @Override
        public Map<String, Object> getRequestParameters() {
            // TODO Auto-generated method stub
            return request;
        }

        @Override
        public <V> V getRequestParameter(String name) {

            return (V) request.get(name);
        }

        @Override
        public ConnectorResponse execute() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getConnection() {
            // TODO Auto-generated method stub
            return null;
        }

    }
}

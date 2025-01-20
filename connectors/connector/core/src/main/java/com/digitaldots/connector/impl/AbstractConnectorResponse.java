/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.impl;

import java.util.HashMap;
import java.util.Map;

import com.digitaldots.connector.spi.ConnectorResponse;

public abstract class AbstractConnectorResponse implements ConnectorResponse {

    protected Map<String, Object> responseParameters;

    public Map<String, Object> getResponseParameters() {
        if (responseParameters == null) {
            responseParameters = new HashMap<>();
            collectResponseParameters(responseParameters);
        }
        return responseParameters;
    }

    @SuppressWarnings("unchecked")
    public <V> V getResponseParameter(String name) {
        return (V) getResponseParameters().get(name);
    }

    /**
     * To be implemented by subclasses for collecting the generic response parameters of a response.
     * 
     * @param responseParameters a map to save the response parameters in
     */
    protected abstract void collectResponseParameters(Map<String, Object> responseParameters);

}

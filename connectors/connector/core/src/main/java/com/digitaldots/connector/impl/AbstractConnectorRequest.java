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

import com.digitaldots.connector.spi.Connector;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.connector.spi.ConnectorResponse;

public abstract class AbstractConnectorRequest<R extends ConnectorResponse> implements ConnectorRequest<R> {

    protected Connector<AbstractConnectorRequest<? extends ConnectorResponse>> connector;

    protected Map<String, Object> requestParameters = new HashMap<>();

    protected AbstractConnectorRequest(Connector<AbstractConnectorRequest<? extends ConnectorResponse>> connector) {
        this.connector = connector;
    }

    public R execute() {
        if (!isRequestValid()) {
            throw new IllegalArgumentException("The request is invalid");
        }
        return connector.execute(this);
    }

    /**
     * Allows subclasses to provide custom validation logic of the request parameters.
     * 
     * @return true if the request parameters are valid.
     */
    protected boolean isRequestValid() {
        return true;
    }

    public void setRequestParameters(Map<String, Object> params) {
        requestParameters = params;
    }

    /**
     * Sets a request parameter on the request
     * 
     * @param name  the name of the parameter
     * @param value the value of the parameter
     */
    public void setRequestParameter(String name, Object value) {
        requestParameters.put(name, value);
    }

    /**
     * @return the parameters as handed in to the request.
     */
    public Map<String, Object> getRequestParameters() {
        return requestParameters;
    }

    @SuppressWarnings("unchecked")
    public <V> V getRequestParameter(String name) {
        return (V) requestParameters.get(name);
    }

    @Override
    public Object getConnection() {
        return null;
    }

}

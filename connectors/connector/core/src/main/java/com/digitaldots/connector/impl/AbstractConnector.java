/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.digitaldots.connector.spi.Connector;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.connector.spi.ConnectorRequestInterceptor;
import com.digitaldots.connector.spi.ConnectorResponse;

/**
 * Abstract implementation of the connector interface.
 *
 * This implementation provides a linked list of interceptors and related methods for handling interceptor invocation.
 *
 *
 */
public abstract class AbstractConnector<Q extends ConnectorRequest<R>, R extends ConnectorResponse> implements Connector<Q> {

    protected String connectorId;

    /**
     * The {@link ConnectorRequestInterceptor} chain
     */
    protected List<ConnectorRequestInterceptor> requestInterceptors = new LinkedList<>();

    protected AbstractConnector(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getId() {
        return connectorId;
    }

    public List<ConnectorRequestInterceptor> getRequestInterceptors() {
        return requestInterceptors;
    }

    public void setRequestInterceptors(List<ConnectorRequestInterceptor> requestInterceptors) {
        this.requestInterceptors = requestInterceptors;
    }

    public Connector<Q> addRequestInterceptor(ConnectorRequestInterceptor interceptor) {
        requestInterceptors.add(interceptor);
        return this;
    }

    public Connector<Q> addRequestInterceptors(Collection<ConnectorRequestInterceptor> interceptors) {
        requestInterceptors.addAll(interceptors);
        return this;
    }

}
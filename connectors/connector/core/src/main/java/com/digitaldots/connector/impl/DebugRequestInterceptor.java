/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.impl;

import com.digitaldots.connector.spi.ConnectorInvocation;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.connector.spi.ConnectorRequestInterceptor;

/**
 * <p>
 * A dummy debug connector, which saves the {@link ConnectorRequest} ({@link #getRequest()}) and the raw request ({@link #getTarget()}) for
 * debugging purpose.
 * </p>
 *
 * <p>
 * The boolean constructor flag determines whether the request invocation should be continued or aborted after this interceptor. Also it is
 * possible to add a response object which will returned without further passing the interceptor chain;
 * </p>
 *
 */
public class DebugRequestInterceptor implements ConnectorRequestInterceptor {

    protected Object response;
    protected boolean proceed;

    private ConnectorRequest<?> request;
    private Object target;

    public DebugRequestInterceptor() {
        this(true);
    }

    public DebugRequestInterceptor(boolean proceed) {
        this.proceed = proceed;
    }

    public DebugRequestInterceptor(Object response) {
        this.response = response;
        this.proceed = false;
    }

    public Object handleInvocation(ConnectorInvocation invocation) {
        request = invocation.getRequest();
        target = invocation.getTarget();
        if (proceed) {
            return invocation.proceed();
        } else {
            return response;
        }
    }

    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }

    public boolean isProceed() {
        return proceed;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public <T> T getResponse() {
        return (T) response;
    }

    public <T extends ConnectorRequest<?>> T getRequest() {
        return (T) request;
    }

    public <T> T getTarget() {
        return (T) target;
    }

    public void setRequest(ConnectorRequest<?> request) {
        this.request = request;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}

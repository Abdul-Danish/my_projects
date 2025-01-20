/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.impl;

import java.util.List;

import com.digitaldots.connector.spi.ConnectorInvocation;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.connector.spi.ConnectorRequestInterceptor;
import com.digitaldots.connector.spi.ConnectorResponse;

/**
 * A simple invocation implementation routing a request through a chain of interceptors. Implementations must implement the
 * {@link #invokeTarget()} method in order to implement the actual request execution / target invocation.
 *
 *
 */
public abstract class AbstractRequestInvocation<R extends ConnectorRequest<? extends ConnectorResponse>> implements ConnectorInvocation<R> {

    protected R target;

    protected int currentIndex;

    protected List<ConnectorRequestInterceptor> interceptorChain;

    protected R request;

    public R getTarget() {
        return target;
    }

    public void setTarget(R t) {
        this.target = t;
    }

    public R getRequest() {
        return request;
    }

    public void setRequest(R request) {
        this.request = request;
    }

    public Object proceed() {
        currentIndex++;
        if (interceptorChain.size() > currentIndex) {
            return interceptorChain.get(currentIndex).handleInvocation(this);

        } else {
            return invokeTarget();

        }
    }

    public abstract Object invokeTarget();

    public void setInterceptors(List<ConnectorRequestInterceptor> interceptors) {
        this.interceptorChain = interceptors;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public List<ConnectorRequestInterceptor> getInterceptorChain() {
        return interceptorChain;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void setInterceptorChain(List<ConnectorRequestInterceptor> interceptorChain) {
        this.interceptorChain = interceptorChain;
    }

}

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.spi;

/**
 * Represents a request invocation / request execution.
 *
 * @see ConnectorRequestInterceptor#handleInvocation(ConnectorInvocation)
 *
 *
 */
public interface ConnectorInvocation<R extends ConnectorRequest<? extends ConnectorResponse>> {

    /**
     * The underlying raw request.
     * 
     * @return the raw request as executed by the connector
     */
    Object getTarget();

    /**
     * <p>
     * The connector request as created through the API. Accessing the request from an interceptor may be useful for setting additional
     * properties on the raw request (returned by {@link #getTarget()}) that are not supported by the connector.
     * </p>
     *
     * <p>
     * NOTE: setting parameters on the request (via) {@link ConnectorRequest#setRequestParameter(String, Object)} will not have any effects
     * once the request is executed.
     * </p>
     *
     * @return the connector request
     */
    R getRequest();

    /**
     * Makes the request proceed through the interceptor chain. {@link ConnectorRequestInterceptor} implementations are responsible for
     * calling this method on the invocation.
     *
     * @return the result of the invocation.
     * @throws Exception
     */
    Object proceed();

}

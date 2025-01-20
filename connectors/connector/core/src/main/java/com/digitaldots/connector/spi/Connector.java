/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.spi;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * A connector represents a way to interact with some system, library or technology. Examples of connectors are connectors for HTTP
 * interaction, a connector to a third-party library like a rules engine or file system abstraction.
 * </p>
 *
 * <p>
 * Once an instance of a connector is created, it is thread-safe, meaning that single connector instance my be used by multiple threads
 * concurrently. Each thread will create new {@link ConnectorRequest Requests} which are not thread-safe and must not be shared among
 * multiple threads.
 * </p>
 *
 * <p>
 * Connectors support interceptors. Interceptors may be used for customizing connector behavior or for adding management aspects such as
 * logging to a connector.
 * </p>
 *
 *
 */
public interface Connector<Q extends ConnectorRequest<? extends ConnectorResponse>> {

    /**
     * The unique Id for the connector.
     *
     * @return the unique connector id
     */
    String getId();

    /**
     * Create a request on the connector.
     *
     * @return the connector-specific request object.
     */
    Q createRequest();

    /**
     * Returns the {@link ConnectorRequestInterceptor} chain for this connector. The implementation will return the actual list,
     * modifications on the list will be reflected in the internal connector state. This means that you can use the returned list to modify
     * the connector's {@link ConnectorRequestInterceptor} chain.
     *
     * @return the list of interceptors.
     */
    List<ConnectorRequestInterceptor> getRequestInterceptors();

    /**
     * Sets the {@link ConnectorRequestInterceptor}s of this connector. The interceptors are invoked for all requests created by the
     * connector.
     */
    void setRequestInterceptors(List<ConnectorRequestInterceptor> requestInterceptors);

    /**
     * Adds a {@link ConnectorRequestInterceptor} to this connector. The interceptor is added at the end of the interceptor chain. The
     * interceptor is invoked for all requests created by the connector.
     */
    Connector<Q> addRequestInterceptor(ConnectorRequestInterceptor interceptor);

    /**
     * Adds a collection of {@link ConnectorRequestInterceptor} to this connector. The interceptors are added at the end of the interceptor
     * chain. The interceptor is invoked for all requests created by the connector.
     */
    Connector<Q> addRequestInterceptors(Collection<ConnectorRequestInterceptor> interceptors);

    /**
     * Execute the request on the connector.
     * 
     * @param request the request
     * @return the result.
     */
    <T extends ConnectorResponse> T execute(Q request);
}
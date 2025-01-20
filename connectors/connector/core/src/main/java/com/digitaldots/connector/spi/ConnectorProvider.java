/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.spi;

/**
 * Provides a {@link Connector} with a unique id.
 */
public interface ConnectorProvider<C extends Connector<? extends ConnectorRequest<? extends ConnectorResponse>>> {

    /** Returns the unique id of the connector created by this factory. */
    String getConnectorId();

    /** Create a new instance of the connector created by this factory. */
    C createConnectorInstance();

}

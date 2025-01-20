/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.spi;

/**
 * Can be used to configure connectors of a certain type. An implementation will be supplied with all discovered connectors of the specified
 * class. {@link Connector#getId()} may be used for further restrict configuration to connectors with a specific id.
 */
public interface ConnectorConfigurator<C extends Connector<? extends ConnectorRequest<? extends ConnectorResponse>>> {

    /**
     * @return the class of connectors this configurator can configure (including subclasses)
     */
    Class<C> getConnectorClass();

    /**
     * Configures the connector instance. This method is invoked with all connectors of the specified class.
     *
     * @param connector the connector instance to configure
     */
    void configure(C connector);

}

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.spi;

/**
 * <p>
 * A connector response which represents an open resource (link to open resource) which must be closed. Consumers of this API are
 * responsible to close the request.
 * </p>
 *
 *
 */
public interface CloseableConnectorResponse extends ConnectorResponse {

    public void close();

}

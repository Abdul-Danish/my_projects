/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector;

/**
 * Indicates an error during the request of a connector.
 */
public class ConnectorRequestException extends ConnectorException {

    private static final long serialVersionUID = 1L;

    public ConnectorRequestException(String message) {
        super(message);
    }

    public ConnectorRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}

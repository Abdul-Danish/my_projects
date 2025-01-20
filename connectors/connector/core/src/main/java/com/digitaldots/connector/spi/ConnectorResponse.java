/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.spi;

import java.util.Map;

/**
 * A connector response representing the result of a connector invocation.
 *
 *
 */
public interface ConnectorResponse {

    /**
     * Retrieves the map of output parameters from the response.
     * 
     * @return the map of output parameters.
     */
    Map<String, Object> getResponseParameters();

    /**
     * Returns the value of a response parameter or 'null' if no such parameter is set.
     *
     * @param name the name of the response parameter
     * @return the value of the response parameter of null.
     */
    <V> V getResponseParameter(String name);

}

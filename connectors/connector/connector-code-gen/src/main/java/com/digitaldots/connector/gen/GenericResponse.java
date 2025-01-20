/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.gen;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.spi.ConnectorResponse;

@Component
public interface GenericResponse extends ConnectorResponse {

    String STATUS_CODE = "statusCode";
    String RESPONSE = "response";

    /**
     * @return the status code of the response
     */
    Integer getStatusCode();

    /**
     * @return the response body or null if non exists
     */
    JSONObject getResponse();

}

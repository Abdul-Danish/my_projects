/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.gen.impl;

import java.util.Map;

import org.json.JSONObject;

import com.digitaldots.connector.gen.GenericResponse;
import com.digitaldots.connector.impl.AbstractConnectorResponse;

import lombok.Getter;

@Getter
public class GenericResponseImpl extends AbstractConnectorResponse implements GenericResponse {
    private static final int STATUS_CODE_VALUE = 400;
    private JSONObject serviceResonse;
    private Exception serviceException;

    public GenericResponseImpl() {
    }

    public GenericResponseImpl(JSONObject serviceResonse) {
        this.serviceResonse = serviceResonse;
    }

    public GenericResponseImpl(Exception serviceException) {
        this.serviceException = serviceException;
    }

    public Integer getStatusCode() {
        return getResponseParameter(STATUS_CODE);
    }

    public JSONObject getResponse() {
        return getResponseParameter(RESPONSE);
    }

    @Override
    protected void collectResponseParameters(Map<String, Object> responseParameters) {
        if (serviceException != null) {
            responseParameters.put(STATUS_CODE, STATUS_CODE_VALUE);
        } else {

            responseParameters.put(STATUS_CODE, serviceResonse.get(STATUS_CODE));
            responseParameters.put(RESPONSE, serviceResonse.get(RESPONSE));
        }
    }
}

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script.service.impl;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.digitaldots.connector.script.exception.ScriptExecutionException;
import com.digitaldots.connector.script.service.ScriptExecutionHelper;
import com.digitaldots.connector.script.service.ScriptHelperRequest;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JoltScriptExecutionHelper implements ScriptExecutionHelper {

    @Override
    public JSONObject execute(ScriptHelperRequest request) {
        log.info("REQUEST {}", request);
        try {
            List<Object> specs = JsonUtils.jsonToList(request.getScript());
            Chainr chainr = Chainr.fromSpec(specs);
            Object inputJSON = JsonUtils.jsonToObject(request.getPayload().toString());
            Object transformedOutput = chainr.transform(inputJSON);

            return new JSONObject(JsonUtils.toPrettyJsonString(transformedOutput));
        } catch (JSONException e) {
            log.error("exception while executing jolt script engine, {}", e);
            throw new ScriptExecutionException("exception while processing jolt script engine", e);
        }
    }

    @Override
    public String type() {
        return "jolt";
    }
}

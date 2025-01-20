/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.script.service.ScriptExecutionHelper;
import com.digitaldots.connector.script.service.ScriptHelperRequest;

@Component
public class ScriptExecutionHelperFactory {

    @Autowired
    List<ScriptExecutionHelper> helpers;

    Map<String, ScriptExecutionHelper> lookupHelpers;

    @Autowired
    @Qualifier("Default")
    ScriptExecutionHelper helper;

    @PostConstruct
    private void init() {
        lookupHelpers = new HashMap<>();
        helpers.stream().forEach(h -> lookupHelpers.put(h.type(), h));
    }

    public JSONObject execute(ScriptHelperRequest request) {
        return lookupHelpers.getOrDefault(request.getScriptFormat(), helper).execute(request);
    }
}

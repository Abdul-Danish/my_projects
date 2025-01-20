/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script.service.impl;

import java.util.List;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.json.JSONObject;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.script.service.ScriptHelperRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class DefaultScriptExecutionHelperTest {

//    @Test
    void executeScript() {
        ScriptEngineManager manager = new ScriptEngineManager();
        List<ScriptEngineFactory> factories = manager.getEngineFactories();
        System.out.println("Factories Available " + factories.size() + "\n");
        for (ScriptEngineFactory factory : factories) {
            System.out.println("ENGINE NAME:- " + factory.getEngineName());
            System.out.println("LANGUAGE NAME:- " + factory.getLanguageName() + "\n");
        }

        System.out.println("----------------------------------------------------------------------------------------------");

        DefaultScriptExecutionHelper executionHelper = new DefaultScriptExecutionHelper();
        System.out.println("TEST CASE RESPONSE " + executionHelper.execute(getHelper()));

    }

    private ScriptHelperRequest getHelper() {
        ObjectMapper mapper = new ObjectMapper();

        String input = "[\n" + "  {\n" + "    \"appName\": \"TELUS Health MyCare\",\n" + "    \"updatedAt\": \"2021-07-19 04:43:45\"\n"
            + "  },\n" + "  {\n" + "    \"appName\": \"TELUS Health MyCare\",\n" + "    \"updatedAt\": \"2021-07-19 04:43:45\"\n" + "  }\n"
            + "]";
        String script = "var results = payload;\n" + "        var reviews = [];\n" + "        for (i = 0; i < results.length; i++) {\n"
            + "            var review = {};\n" + "            review.appName = results[i].appName;\n"
            + "            reviews.push(review);\n" + "        }\n" + "        reviews;";
        String scriptFormat = "javascript";

        // String script = "x=payload;\n"
        // + "z={};\n"
        // + "result=[];\n"
        // + "test=x[0];\n"
        // + "for y in x:\n"
        // + " z.update({'appName': y.get('appName')})\n"
        // + " result.append(z)\n"
        // + "result";
        // String scriptFormat = "python";

        Object payload = new JSONObject();
        try {
            payload = mapper.readValue(input, Object.class);
        } catch (JsonProcessingException e) {
            throw new ConnectorException("Default Script Ececution Error", e);
        }
        return ScriptHelperRequest.builder().scriptFormat(scriptFormat).script(script).payload(payload).build();
    }

}

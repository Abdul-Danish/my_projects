/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.digitaldots.connector.script.service.ScriptHelperRequest;
import com.digitaldots.connector.script.service.impl.JoltScriptExecutionHelper;

class JoltScriptExecutionHelperTest {

    @Test
    void testExecute() {
        String input = "{\"ratings\":{\"primary\":5,\"quality\":4,\"design\":5}}";
        String spec = "[{\"operation\":\"shift\",\"spec\":{\"ratings\":{\"*\":{\"$\":\"Ratings[#2].Name\",\"@\":\"Ratings[#2].Value\"}}}}]";
        JSONObject inputJO = new JSONObject(input);

        ScriptHelperRequest scriptHelperRequest = ScriptHelperRequest.builder().scriptFormat("jolt").script(spec).payload(inputJO).build();
        JoltScriptExecutionHelper executionHelper = new JoltScriptExecutionHelper();

        JSONObject response = executionHelper.execute(scriptHelperRequest);
        assertThat(response).isNotNull();
    }

}

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script.service;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScriptHelperRequest {

    private String scriptFormat;

    private String script;

    private Object payload;

    private Map<String, Object> vars;

}

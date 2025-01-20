/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */
package com.digitaldots.process.variables;

import java.util.List;

import com.digitaldots.platform.process.entity.Message;
import com.digitaldots.process.elements.Parameter;

public interface Variables {

    <T> T getForObject(String expression, Class<T> responseType, Object event);

    Message process(List<Parameter> ioParams, Message event);
}

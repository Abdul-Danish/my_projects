/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Function {
    private String solutionId;
    private String alternateId;
    private String version;
    private String gateway;

    public String generateGateWayUrl() {
        return this.getGateway().concat("/").concat("s" + solutionId).concat("-").concat(alternateId).concat("-")
            .concat(version.replace(".", "-"));
    }
}

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.process.api.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorConfig {

    private String name;
    private String alias;
    private String alternateId;
    private String id;
    private String description;
    private String category;
    private String template;
    private String templateVersion;
    private String type;
    private String group;
    private Boolean active;
    private String version;
    private List<String> versionHistory;
    @Builder.Default
    private Map<String, Object> properties = new HashMap<>();
}

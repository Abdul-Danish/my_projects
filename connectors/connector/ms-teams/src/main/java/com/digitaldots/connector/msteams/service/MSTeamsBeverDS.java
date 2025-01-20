/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.msteams.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.graphapi.service.GraphApiBeverDS;
import com.digitaldots.connector.graphapi.util.GraphApiHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MSTeamsBeverDS extends GraphApiBeverDS {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public String getDBTye() {
        return "Teams";
    }

    @Override
    public Object validate(GraphApiHelper graphApiHelper, Map<String, Object> parameters) {
        log.debug("GRAPH API HELPER {}", graphApiHelper);
        log.debug("PROPERTIES {}", parameters);
        MSTeamsService teamsService = new MSTeamsService();
        teamsService.graphApiHelper = graphApiHelper;
        teamsService.teamId = (String) parameters.get("team");
        teamsService.channelId = (String) parameters.get("channel");
        teamsService.contentType = (String) parameters.get("contentType");
        teamsService.messageContent = mapper.convertValue(parameters.get("content"), new TypeReference<Map<String, Object>>() {
        });
        teamsService.execute(null);

        return "Success";
    }
}

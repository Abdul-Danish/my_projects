/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.scraper;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.cache.Store;

@Component
public class ScraperBeverDS implements Store<ScraperHelper, ScraperHelper> {

    @Value("${scraper.basePath}")
    private String basePath;

    @Override
    public String getDBTye() {
        return "SCRAPERS";
    }

    @Override
    public ScraperHelper getConnection(ScraperHelper scraperHelper) {
        return scraperHelper;
    }

    @Override
    public ScraperHelper getDataSource(Map<String, Object> configProperties) {
        if (Objects.isNull(configProperties.getOrDefault("alias", configProperties.get("project")))) {
            throw new ConnectorException("Project/alias not provided");
        }
        if (Objects.isNull(configProperties.get("spider"))) {
            throw new ConnectorException("Spider not provided");
        }
        if (Objects.nonNull(configProperties.get("name"))) {
            configProperties.remove("name");
        }
        if (Objects.nonNull(configProperties.get("version"))) {
            configProperties.remove("version");
        }
        return ScraperHelper.builder().project((String) configProperties.getOrDefault("alias", configProperties.get("project")))
            .spider(configProperties.get("spider").toString()).baseUrl(basePath).configuration(configProperties).build();
    }

}

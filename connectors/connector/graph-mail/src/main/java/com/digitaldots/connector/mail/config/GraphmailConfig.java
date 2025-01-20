/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.mail.config;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.graphapi.service.GraphApiBeverDS;
import com.digitaldots.connector.graphapi.util.GraphApiHelper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GraphmailConfig extends GraphApiBeverDS {

    @Override
    public String getDBTye() {
        return "Email";
    }

    @Override
    public GraphApiHelper getDataSource(Map<String, Object> configProperties) {
        String userMail = (String) (configProperties.get("userMail"));
        if (!StringUtils.hasLength(userMail)) {
            log.error("Exception occured whule retrieving datasource, 'User Mail' field should not be empty or null");
            throw new ConnectorException("'User Mail' field should not be empty or null");
        }
        GraphApiHelper dataSource = super.getDataSource(configProperties);
        return EmailApiHelper.emailBuilder().graphClient(dataSource.getGraphClient()).url(dataSource.getUrl())
            .webhookUrl(dataSource.getWebhookUrl()).userMail(userMail).build();
    }

}

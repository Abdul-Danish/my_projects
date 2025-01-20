/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.mail.config;

import com.digitaldots.connector.graphapi.util.GraphApiHelper;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class EmailApiHelper extends GraphApiHelper {
    private String userMail;

    @Builder(builderMethodName = "emailBuilder")
    public EmailApiHelper(GraphServiceClient graphClient, String url, String webhookUrl, String userMail) {
        super(graphClient, url, webhookUrl);
        this.userMail = userMail;
    }
}

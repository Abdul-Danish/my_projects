/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.util;

import com.microsoft.graph.serviceclient.GraphServiceClient;

import lombok.Data;

@Data
public class SharepointHelper {

    private GraphServiceClient graphClient;
    private String siteId;
    private String accessToken;
    private String siteDomain;

}

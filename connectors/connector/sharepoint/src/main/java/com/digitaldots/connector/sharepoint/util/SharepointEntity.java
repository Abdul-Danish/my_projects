/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SharepointEntity {

    private String id;
    private String name;
    private String description;
    private String displayName;
    private String webUrl;
    private String type;
    private String itemCount;
    private String exists;
    private String attachments;
    private String createdBy;
    private String modifiedBy;
    private String createdAt;
    private String modifiedAt;
}

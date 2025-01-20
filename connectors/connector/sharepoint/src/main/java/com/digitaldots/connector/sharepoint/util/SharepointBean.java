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
public class SharepointBean {

    public String method;
    public String folderName;
    public String listTitle;
    public String description;
    public String siteName;
    public String loginName;
    public Permission permission;
    public String groupName;
    public byte[] file;
    public String fileName;
    public String serverRelatUrl;
    public String rootFolder;

}

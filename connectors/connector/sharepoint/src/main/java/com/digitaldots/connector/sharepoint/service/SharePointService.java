/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.service;

import org.json.JSONObject;

import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.sharepoint.util.Permission;
import com.digitaldots.connector.sharepoint.util.SharepointBean;
import com.digitaldots.connector.sharepoint.util.SharepointHelper;
import com.digitaldots.connector.spi.ConnectorRequest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Connector(id = "SharePoint")
@Slf4j
@Getter
public class SharePointService {

    @Connection
    protected SharepointHelper sharepointHelper;

    @Params(name = "method")
    protected String method;

    @Params(name = "folderName")
    protected String folderName;

    @Params(name = "listTitle")
    protected String listTitle;

    @Params(name = "description")
    protected String description;

    @Params(name = "siteName")
    protected String siteName;

    @Params(name = "loginName")
    protected String loginName;

    @Params(name = "permission")
    protected Permission permission;

    @Params(name = "groupName")
    protected String groupName;

    @Params(name = "file")
    protected byte[] file;

    @Params(name = "fileName")
    protected String fileName;

    @Params(name = "serverRelatUrl")
    protected String serverRelatUrl;

    @Params(name = "rootFolder")
    protected String rootFolder;

    private SharepointGraphClient graphClient = new SharepointGraphClient();
    private SharepointRest sharepointRest = new SharepointRest();

    @Execute
    public JSONObject execute(ConnectorRequest<?> request) {

        JSONObject response;
        log.debug("sharepointHelper : {}", sharepointHelper);

        SharepointBean bean = SharepointBean.builder().method(this.method).folderName(this.folderName).listTitle(this.listTitle)
            .description(this.description).siteName(this.siteName).loginName(this.loginName).permission(this.permission)
            .groupName(this.groupName).file(this.file).fileName(this.fileName).serverRelatUrl(this.serverRelatUrl)
            .rootFolder(this.rootFolder).build();

        if (sharepointHelper.getSiteId() != null) {
            response = graphClient.execute(bean, sharepointHelper);
        } else {
            response = sharepointRest.execute(bean, sharepointHelper);
        }

        return response;
    }

}
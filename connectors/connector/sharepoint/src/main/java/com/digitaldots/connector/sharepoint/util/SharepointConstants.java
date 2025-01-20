/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.util;

import java.net.URI;
import java.net.URISyntaxException;

import com.digitaldots.connector.ConnectorException;

public class SharepointConstants {

    public static final String CLIENT_CREDENTIAL_AUTH = "client-credentials-authentication";
    public static final String SITE_CREDENTIAL_AUTH = "site-credentials-authentication";
    public static final String AUTH_METHOD = "authMethod";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String SITEDOMAIN = "siteDomain";
    public static final String ACCEPTS = "application/json;odata=verbose";
    public static final String CONTENT_TYPE = "application/json;odata=verbose";

    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String RESOURCE = "resource";
    public static final String TENANT_ID = "tenantId";
    public static final String USER_NAME = "authUserName";
    public static final String USER_PWD = "authPassword";

    public static final String CREDENTIAL_TENANT_ID = "clientTenantId";
    public static final String CREDENTIAL_CLIENT_ID = "authUserName";
    public static final String CREDENTIAL_CLIENT_SECRET = "authPassword";
    public static final String SITE_ID = "siteId";

    public static final String URL_ENCODED = "application/x-www-form-urlencoded";

    public static final String SP_SITEURL = "/sites/";

    public static final String getResource(Object resource, Object siteDomain, Object tenantId) {
        return resource + "/" + siteDomain + "@" + tenantId;
    }

    public static String getClientId(Object clientId, Object tenantId) {
        return clientId + "@" + tenantId;
    }

    public static URI getAuthUrl(Object tenantId) {
        try {
            return new URI("https", "//accounts.accesscontrol.windows.net/" + tenantId + "/tokens/OAuth/2/", null);
        } catch (URISyntaxException e) {
            throw new ConnectorException("Exception while request to sharepoint", e);
        }
    }

    private SharepointConstants() {

    }

}

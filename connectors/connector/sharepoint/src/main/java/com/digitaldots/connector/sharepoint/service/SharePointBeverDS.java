/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.service;

import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.cache.Store;
import com.digitaldots.connector.sharepoint.util.SharepointConstants;
import com.digitaldots.connector.sharepoint.util.SharepointHelper;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SharePointBeverDS implements Store<SharepointHelper, SharepointHelper> {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getDBTye() {
        return "SharePoint";
    }

    @Override
    public SharepointHelper getConnection(SharepointHelper sharepointHelper) {
        log.debug("configProperties {}", sharepointHelper);
        return sharepointHelper;
    }

    @Override
    public SharepointHelper getDataSource(Map<String, Object> configProperties) {
        log.debug("configProperties {}", configProperties);
        if (!configProperties.containsKey((SharepointConstants.AUTH_METHOD))) {
            throw new ConnectorException("Authentication is missing");
        }
        SharepointHelper sharepointHelper = new SharepointHelper();
        try {
            if (Objects.equals(SharepointConstants.CLIENT_CREDENTIAL_AUTH, configProperties.get(SharepointConstants.AUTH_METHOD))) {
                final ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                    .clientId(configProperties.get(SharepointConstants.CREDENTIAL_CLIENT_ID).toString())
                    .clientSecret(configProperties.get(SharepointConstants.CREDENTIAL_CLIENT_SECRET).toString())
                    .tenantId(configProperties.get(SharepointConstants.CREDENTIAL_TENANT_ID).toString()).build();

                // TokenCredentialAuthProvider authenticationProvider = new TokenCredentialAuthProvider(credential);
                // sharepointHelper.setGraphClient(GraphServiceClient.builder().authenticationProvider(authenticationProvider).buildClient());

                GraphServiceClient graphServiceClient = new GraphServiceClient(credential);
                sharepointHelper.setGraphClient(graphServiceClient);
                sharepointHelper.setSiteId(configProperties.get(SharepointConstants.SITE_ID).toString());
            } else {
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add(SharepointConstants.GRANT_TYPE, configProperties.get(SharepointConstants.GRANT_TYPE).toString());
                params.add(SharepointConstants.CLIENT_ID, SharepointConstants
                    .getClientId(configProperties.get(SharepointConstants.USER_NAME), configProperties.get(SharepointConstants.TENANT_ID)));
                params.add(SharepointConstants.CLIENT_SECRET, configProperties.get(SharepointConstants.USER_PWD).toString());
                params.add(SharepointConstants.RESOURCE, SharepointConstants.getResource(configProperties.get(SharepointConstants.RESOURCE),
                    configProperties.get(SharepointConstants.SITEDOMAIN), configProperties.get(SharepointConstants.TENANT_ID)));

                LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                headers.add(HttpHeaders.ACCEPT, SharepointConstants.ACCEPTS);
                headers.add(HttpHeaders.CONTENT_TYPE, SharepointConstants.URL_ENCODED);

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    SharepointConstants.getAuthUrl(configProperties.get(SharepointConstants.TENANT_ID)), request, String.class);

                if (responseEntity.hasBody()) {
                    JSONObject jsonObj = new JSONObject(responseEntity.getBody());
                    sharepointHelper.setAccessToken(jsonObj.get(SharepointConstants.ACCESS_TOKEN).toString());
                    sharepointHelper.setSiteDomain(configProperties.get(SharepointConstants.SITEDOMAIN).toString());
                }
            }
        } catch (Exception e) {
            log.error("Exception occured while retrieving datasource, Authentication has failed", e);
            throw new ConnectorException("Authentication failed ", e);
        }
        log.debug("sharepointHelper : {}", sharepointHelper);
        return sharepointHelper;
    }
}

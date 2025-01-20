/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sharepoint.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.digitaldots.connector.sharepoint.util.Permission;
import com.digitaldots.connector.sharepoint.util.SharepointConstants;
import com.digitaldots.connector.sharepoint.util.SharepointHelper;
import com.digitaldots.connector.spi.ConnectorRequest;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class SharePointConnectorTest {

    @Autowired
    static ConnectorRequest<?> connectorRequest;

    private Map<String, Object> getTokenMap() {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("grant_type", "client_credentials");
        tokenMap.put("client_id", "70ffda8b-6b40-43cc-9c45-ae26b93a6def");
        tokenMap.put("client_secret", "yOQ6Fj+plrD7rNPE/pXMSyMqpSq1F67huMJBDDDImYU=");
        tokenMap.put("resource", "00000003-0000-0ff1-ce00-000000000000");
        tokenMap.put("siteDomain", "digitaldotsinc.sharepoint.com");
        tokenMap.put("tenantId", "09d9df66-37d7-493b-8823-4af2c12a1af5");
        return tokenMap;
    }

    @Test
    public void createList() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "createList";
        service.folderName = "TestList6";
        service.description = "Entry using Connector";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void createListItem() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "createListItem";
        service.folderName = "TestList";
        service.listTitle = "Entry using Connector";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void getAllLists() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "getAllLists";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void getListByTitle() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "getListByTitle";
        service.folderName = "TestList";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void uploadFileToList() throws IOException {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "uploadFileToList";
        service.folderName = "TestList5";
        service.listTitle = "Entry using Connector";

        File file = new File("src/test/resources/HelloUser.xlsx");
        byte[] fileContent = Files.readAllBytes(file.toPath());
        service.file = Base64.getEncoder().encode(fileContent);
        service.fileName = file.getName();

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void createFolder() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "createFolder";
        service.folderName = "testFolder3";
        service.serverRelatUrl = "/sites/sharepointsite/DocLib";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void countFolderItems() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "countFolderItems";
        service.serverRelatUrl = "/sites/sharepointsite/DocLib";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void uploadFileToFolder() throws IOException {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "uploadFileToFolder";
        service.serverRelatUrl = "/sites/sharepointsite/DocLib";
        service.folderName = "testFolder3";

        File file = new File("src/test/resources/HelloUser.xlsx");
        byte[] fileContent = Files.readAllBytes(file.toPath());
        service.file = Base64.getEncoder().encode(fileContent);
        service.fileName = file.getName();

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void addUserToFolder() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "addUserToFolder";
        service.loginName = "amritapanda@digitaldots.ai";
        service.permission = Permission.FULLCONTROL;
        service.serverRelatUrl = "/sites/sharepointsite/DocLib";
        service.folderName = "testFolder3";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void addUserToGroup() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.siteName = "sharepointsite";
        service.method = "addUserToGroup";
        service.loginName = "amritapanda@digitaldots.ai";
        service.groupName = "GroupTest";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    private SharepointHelper getConnection(Map<String, Object> configProperties) {

        SharepointHelper sharepointHelper = new SharepointHelper();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", configProperties.get("grant_type").toString());
        params.add("client_id", SharepointConstants.getClientId(configProperties.get(SharepointConstants.CLIENT_ID),
            configProperties.get(SharepointConstants.TENANT_ID)));
        params.add("client_secret", configProperties.get("client_secret").toString());
        params.add(SharepointConstants.RESOURCE, SharepointConstants.getResource(configProperties.get(SharepointConstants.RESOURCE),
            configProperties.get(SharepointConstants.SITEDOMAIN), configProperties.get(SharepointConstants.TENANT_ID)));

        RestTemplate restTemplate = new RestTemplate();
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, "application/json;odata=verbose");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> responseEntity = restTemplate
                .postForEntity(SharepointConstants.getAuthUrl(configProperties.get("tenantId")), request, String.class);
            if (responseEntity.hasBody()) {
                JSONObject jsonObj = new JSONObject(responseEntity.getBody());
                sharepointHelper.setAccessToken(jsonObj.get("access_token").toString());
                sharepointHelper.setSiteDomain(configProperties.get("siteDomain").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sharepointHelper;
    }

}

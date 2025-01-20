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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.digitaldots.connector.sharepoint.util.SharepointConstants;
import com.digitaldots.connector.sharepoint.util.SharepointHelper;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.microsoft.graph.serviceclient.GraphServiceClient;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class SharePointGraphApiConnectorTest {

    @Autowired
    static ConnectorRequest<?> connectorRequest;

    private Map<String, Object> getTokenMap() {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("clientId", "123abd7e-0b99-46cc-9c11-55bab47657ac");
        tokenMap.put("userName", "rahulyadav@digitaldots.ai");
        tokenMap.put("userPassword", "rdd@88#1");
        tokenMap.put("siteId", "digitaldotsinc.sharepoint.com,07251ada-fd96-445e-8dfe-6095e116c8ab,8e53a2f6-d158-4040-9056-7e8c714cf050");
        return tokenMap;
    }

//	@Test
    public void createList() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.method = "createList";
        service.folderName = "Books";
        service.description = "Entry using Connector";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void getAllLists() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.method = "getAllLists";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void createListItem() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.method = "createListItem";
        service.folderName = "Postman";
        service.listTitle = "Entry using Connector";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void getListByTitle() {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
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
        service.method = "uploadFileToList";
        service.folderName = "Folder1/Folder2";
        service.listTitle = "DocLib";

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
        service.method = "createFolder";
        service.folderName = "Folder1";
        service.serverRelatUrl = "DocLib";

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void uploadFileToFolder() throws IOException {
        Map<String, Object> tokenMap = getTokenMap();

        SharePointService service = new SharePointService();
        service.sharepointHelper = getConnection(tokenMap);
        service.method = "uploadFileToFolder";
        service.serverRelatUrl = "DocLib";
        service.folderName = "Folder1/Folder2";

        File file = new File("src/test/resources/HelloUser.xlsx");
        byte[] fileContent = Files.readAllBytes(file.toPath());
        service.file = Base64.getEncoder().encode(fileContent);
        service.fileName = file.getName();

        JSONObject response = service.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    private SharepointHelper getConnection(Map<String, Object> configProperties) {

        SharepointHelper sharepointHelper = new SharepointHelper();
        UsernamePasswordCredential credential = new UsernamePasswordCredentialBuilder()
            .clientId(configProperties.get(SharepointConstants.CLIENT_ID).toString())
            .username(configProperties.get(SharepointConstants.USER_NAME).toString())
            .password(configProperties.get(SharepointConstants.USER_PWD).toString()).build();

        // TokenCredentialAuthProvider authenticationProvider = new TokenCredentialAuthProvider(credential);
        // sharepointHelper.setGraphClient(GraphServiceClient.builder().authenticationProvider(authenticationProvider).buildClient());

        GraphServiceClient graphServiceClient = new GraphServiceClient(credential);
        sharepointHelper.setGraphClient(graphServiceClient);
        sharepointHelper.setSiteId(configProperties.get("siteId").toString());

        return sharepointHelper;
    }

}

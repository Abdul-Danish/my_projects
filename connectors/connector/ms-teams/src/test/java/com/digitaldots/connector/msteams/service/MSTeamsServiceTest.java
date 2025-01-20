/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.msteams.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.digitaldots.api.client.RestClient;
import com.digitaldots.connector.graphapi.service.GraphApiBeverDS;
import com.digitaldots.connector.graphapi.util.GraphApiConstants;
import com.digitaldots.connector.graphapi.util.GraphApiHelper;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.graph.groups.GroupsRequestBuilder;
import com.microsoft.graph.models.Channel;
import com.microsoft.graph.models.ChannelCollectionResponse;
import com.microsoft.graph.models.ChatMessage;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.GroupCollectionResponse;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.teams.TeamsRequestBuilder;
import com.microsoft.graph.teams.item.TeamItemRequestBuilder;
import com.microsoft.graph.teams.item.channels.item.messages.MessagesRequestBuilder;

@SpringBootTest(classes = MSTeamsService.class)
public class MSTeamsServiceTest {

    @Autowired
    private MSTeamsService msTeamsService;

    @MockBean
    private RestClient restClient;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private GraphApiHelper graphApiHelper;

    @MockBean
    private GraphApiBeverDS apiBeverDS;

    static ConnectorRequest<?> connectorRequest;

    @MockBean
    private GraphServiceClient mockGraphClient;

    @MockBean
    private GroupsRequestBuilder mockGroupBuilder;

    @MockBean
    private TeamsRequestBuilder teamsRequestBuilder;

    @MockBean
    private com.microsoft.graph.teams.item.channels.item.ChannelItemRequestBuilder channelItemRequestBuilder;

    @MockBean
    private GroupCollectionResponse groupCollectionResponse;

    @MockBean
    private TeamItemRequestBuilder teamItemRequestBuilder;

    @MockBean
    private com.microsoft.graph.teams.item.channels.ChannelsRequestBuilder channelsRequestBuilder;

    @MockBean
    private ChannelCollectionResponse channelCollectionResponse;

    @MockBean
    private MessagesRequestBuilder messageRequestBuilder;

    @MockBean
    private ChatMessage chatMessage;

    @Test
    void sendWebhookMessageTest() {

        Map<String, Object> messageContent = new HashMap<>();
        messageContent.put("message", "Hello, Teams...");
        msTeamsService.messageContent = messageContent;
        msTeamsService.graphApiHelper = graphApiHelper;
        ResponseEntity<String> response = new ResponseEntity<>("Mock response body", HttpStatus.OK);
        when(restClient.postServiceWithHeaders(anyString(), any(), eq(String.class), any())).thenReturn(response);
        String webhookUrl = "http://ddinc.webhook.office.com/webhookb2/24b3d881-1aef-4c88-b58d-db328e8dca33@09d9df66-37d7-493b-8823-4af2c12a1af5/IncomingWebhook/e675d281fa93413d83c2b3184a7169f2/ef918661-50fb-4764-8aad-9224fe38cb78";
        when(graphApiHelper.getWebhookUrl()).thenReturn(webhookUrl); // Mock url behaviour
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put("webhook", Boolean.TRUE);
        configProperties.put("authMethod", "no-auth");
        configProperties.put("webhookUrl", webhookUrl);
        when(apiBeverDS.getDataSource(configProperties)).thenReturn(graphApiHelper);

        System.out.println(msTeamsService.execute(connectorRequest));
        verify(restClient, times(1)).postServiceWithHeaders(anyString(), any(), eq(String.class), any());
    }

    @Test
    void sendClientMessageTest() {

        msTeamsService.graphApiHelper = graphApiHelper;
        Map<String, Object> messageContent = new HashMap<>();
        messageContent.put("message", "Hello, Teams...");
        msTeamsService.messageContent = messageContent;
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put("authMethod", "username-password");
        configProperties.put(GraphApiConstants.USER_PASSWORD_CREDENTIALS, Boolean.TRUE);
        configProperties.put(GraphApiConstants.UCP_CLIENT_ID, "83b86247-94fa-4c70-beff-f69c35653bed");
        configProperties.put(GraphApiConstants.UCP_AUTH_USERNAME, "rahulyadav@digitaldots.ai");
        configProperties.put(GraphApiConstants.UCP_AUTH_PASSWORD, "rdd@88#1");
        when(apiBeverDS.getDataSource(configProperties)).thenReturn(graphApiHelper);
        when(graphApiHelper.getWebhookUrl()).thenReturn(null);

        java.util.List<Group> groupList = new ArrayList<>();

        Group mockGroup = new Group();
        mockGroup.setId("Teams-id");
        groupList.add(mockGroup);

        when(graphApiHelper.getGraphClient()).thenReturn(mockGraphClient);
        when(mockGraphClient.groups()).thenReturn(mockGroupBuilder);
        when(mockGroupBuilder.get(any())).thenReturn(groupCollectionResponse);
        when(groupCollectionResponse.getValue()).thenReturn(groupList);

        when(mockGraphClient.teams()).thenReturn(teamsRequestBuilder);
        when(teamsRequestBuilder.byTeamId(any())).thenReturn(teamItemRequestBuilder);
        when(teamItemRequestBuilder.channels()).thenReturn(channelsRequestBuilder);
        when(channelsRequestBuilder.byChannelId(any())).thenReturn(channelItemRequestBuilder);
        when(channelsRequestBuilder.get(any())).thenReturn(channelCollectionResponse);

        List<Channel> channelList = new ArrayList<>();
        Channel myChannel = mock(Channel.class);
        myChannel.setId("myChannel");
        channelList.add(myChannel);
        when(channelCollectionResponse.getValue()).thenReturn(channelList);
        when(channelList.get(0).getId()).thenReturn("Testing");
        when(channelItemRequestBuilder.messages()).thenReturn(messageRequestBuilder);

        ChatMessage chatMessage = mock(ChatMessage.class);
        ItemBody itemBody = new ItemBody();
        itemBody.setContent("Teams content");
        when(chatMessage.getBody()).thenReturn(itemBody);
        when(messageRequestBuilder.post(any())).thenReturn(chatMessage);

        msTeamsService.execute(connectorRequest);

        assertEquals("Teams-id", groupList.get(0).getId());
        assertEquals("Testing", channelList.get(0).getId());
        assertEquals("Teams content", chatMessage.getBody().getContent());

    }

}

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.msteams.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.digitaldots.api.client.RestClient;
import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.graphapi.util.GraphApiHelper;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.ChatMessage;
import com.microsoft.graph.models.ChatMessageAttachment;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Connector(id = "Teams")
public class MSTeamsService {

    private static final int BAD_REQUEST_CODE = 400;

    @Connection
    protected GraphApiHelper graphApiHelper;

    @Params(name = "team")
    protected String teamId;

    @Params(name = "channel")
    protected String channelId;

    @Params(name = "contentType")
    protected String contentType;

    @Params(name = "content")
    protected Map<String, Object> messageContent;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestClient restClient;

    @Execute
    public JSONObject execute(ConnectorRequest<?> request) {
        log.debug("contentType {} team {} channel {} messageContent {} ", this.contentType, this.teamId, this.channelId,
            this.messageContent);

        if (graphApiHelper.getWebhookUrl() != null) {
            return sendWebhookMessage();
        } else {
            return sendClientMessage();
        }
    }

    private JSONObject sendWebhookMessage() {
        log.debug("USING WEBHOOK");
        JSONObject response = new JSONObject();
        try {
            String entity = mapper.writeValueAsString(messageContent);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            log.debug("HTTP POST {}", graphApiHelper.getWebhookUrl());
            ResponseEntity<String> httpResponse = restClient.postServiceWithHeaders(graphApiHelper.getWebhookUrl(), headers, String.class,
                entity);
            response.put("statusCode", httpResponse.getStatusCode());
            response.put("entity", httpResponse.getBody());
            if (httpResponse.getStatusCode().value() == BAD_REQUEST_CODE) {
                throw new ConnectorException("Webhook error for palyload -" + entity);
            }
        } catch (IOException e) {
            log.error("Exception occured while sending webhook message", e);
            throw new ConnectorException("exception from teams connector with WEBHOOK ||" + e.getMessage(), e);
        }

        return response;
    }

    private JSONObject sendClientMessage() {
        log.debug("USING GRAPH CLIENT");
        GraphServiceClient graphClient = graphApiHelper.getGraphClient();
        // List<Group> group = graphClient.groups().buildRequest().filter("displayName eq '" + teamId + "'").get().getCurrentPage();
        List<Group> group = graphClient.groups().get(req -> req.queryParameters.filter = "displayName eq '" + teamId + "'").getValue();
        JSONObject response = new JSONObject();
        try {
            ChatMessage chatMessage = constructTeamsMessage();
            // ChatMessage messageResponse = graphClient
            // .teams(group.get(0).id).channels(graphClient.teams(group.get(0).id).channels().buildRequest()
            // .filter("displayName eq '" + channelId + "'").get().getCurrentPage().get(0).id)
            // .messages().buildRequest().post(chatMessage);

            ChatMessage messageResponse = graphClient.teams().byTeamId(group.get(0).getId()).channels()
                .byChannelId(graphClient.teams().byTeamId(group.get(0).getId()).channels()
                    .get(req -> req.queryParameters.filter = "displayName eq '" + channelId + "'").getValue().get(0).getId())
                .messages().post(chatMessage);

            log.debug("SEND MESSAGE RESPONSE {}", messageResponse.toString());
            response.put("id", messageResponse.getId());
            response.put("replyToId", messageResponse.getReplyToId());
            response.put("content", messageResponse.getBody().getContent());
            response.put("contentType", messageResponse.getBody().getContentType());
            response.put("oDataType", messageResponse.getBody().getOdataType());
        } catch (Exception e) {
            log.error("Exception occured while sending client message", e);
            throw new ConnectorException("exception from teams connector with Client ||" + e.getMessage(), e);
        }
        return response;
    }

    private ChatMessage constructTeamsMessage() throws JsonProcessingException {
        ItemBody body = new ItemBody();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSubject(null);
        if (contentType != null) {
            body.setContentType(BodyType.Html);
            String attachmentId = UUID.randomUUID().toString();
            body.setContent("<attachment id=\"" + attachmentId + "\"></attachment>");
            LinkedList<ChatMessageAttachment> attachmentsList = new LinkedList<>();
            ChatMessageAttachment attachments = new ChatMessageAttachment();
            attachments.setId(attachmentId);
            attachments.setContentType(contentType);
            attachments.setContentUrl(null);
            attachments.setContent(mapper.writeValueAsString(messageContent));
            attachments.setName(null);
            attachments.setThumbnailUrl(null);
            attachmentsList.add(attachments);
            chatMessage.setAttachments(attachmentsList);
        } else {
            chatMessage.setAttachments(null);
            body.setContent(mapper.writeValueAsString(messageContent));
        }
        chatMessage.setBody(body);
        return chatMessage;
    }
}

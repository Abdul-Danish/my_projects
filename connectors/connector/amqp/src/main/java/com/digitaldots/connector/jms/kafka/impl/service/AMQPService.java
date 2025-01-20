/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.jms.kafka.impl.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.rabbitmq.client.ConnectionFactory;

import lombok.extern.slf4j.Slf4j;

@Connector(id = "AMQP")
@Slf4j
public class AMQPService {

    private static final int INTERNAL_SERVER_ERROR_CODE = 500;

    @Params(name = "method")
    protected String method;

    @Params(name = "topicName")
    protected String topicName;

    @Params
    protected String payload;

    @Params(name = "headers")
    protected Map<String, String> headers;

    @com.digitaldots.connector.annotation.Connection
    protected ConnectionFactory factory;

    @Execute
    public JSONObject execute(ConnectorRequest<?> request) {
        try {
            log.debug("processing AMQP Message");
            return sendMessageToAmqp();
        } catch (Exception e) {
            log.error("Exception Occurered whilie executing :: ", e);
            JSONObject response = new JSONObject();
            response.put("status", INTERNAL_SERVER_ERROR_CODE);
            response.put("message", e.getMessage());
            response.put("data", new JSONArray());
            return response;
        }
    }

    private JSONObject sendMessageToAmqp() throws IOException, TimeoutException {
        try (com.rabbitmq.client.Connection connection = factory.newConnection();
            com.rabbitmq.client.Channel channel = connection.createChannel()) {
            channel.basicPublish(this.topicName, this.topicName, null, this.payload.getBytes(StandardCharsets.UTF_8));
            log.debug("message sent {}");
            return new JSONObject();
        } catch (Exception e) {
            log.error("Exception occured while sending message to amqp service", e);
            throw new ConnectorException("excepetion from AMQ service send", e);
        }
    }

}

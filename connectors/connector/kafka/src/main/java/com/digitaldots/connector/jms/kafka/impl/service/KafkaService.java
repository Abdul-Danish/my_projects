/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.jms.kafka.impl.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;

import lombok.extern.slf4j.Slf4j;

@Connector(id = "Kafka")
@Slf4j
public class KafkaService {
    @Params(name = "method")
    protected String method;

    @Params(name = "topicName")
    protected String topicName;

    @Params
    protected String payload;

    @Params(name = "headers")
    protected Map<String, String> headers;

    @Connection
    protected KafkaProducer<String, String> msgProducer;

    @Execute
    public JSONObject execute(ConnectorRequest<?> request) {
        if ("send".equals(method)) {
            return sendMessageToKafka();
        } else {
            throw new ConnectorException("Invalid operation for the connetor");
        }
    }

    private JSONObject sendMessageToKafka() {
        try {
            log.debug("sending message to kafka");
            this.msgProducer.beginTransaction();
            sendMessage(this.topicName, this.payload, this.headers);

            return new JSONObject();
        } catch (Exception e) {
            throw new ConnectorException("Error while sending the message to Kafka", e);
        }
    }

    private void sendMessage(String topicName, Object jsonObj, Map<String, String> headers) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, jsonObj.toString());
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                producerRecord.headers().add(header.getKey(), header.getValue().getBytes(StandardCharsets.UTF_8));
            }
        }
        msgProducer.send(producerRecord);
        msgProducer.flush();
        msgProducer.commitTransaction();
    }

}

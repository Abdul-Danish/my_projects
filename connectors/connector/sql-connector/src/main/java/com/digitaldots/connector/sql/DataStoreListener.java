/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.Connectors;
import com.digitaldots.connector.spi.Connector;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.connector.spi.ConnectorResponse;
import com.digitaldots.process.connector.ConnectorCP;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataStoreListener {

    @Value("${category:datastores}")
    private String category;
    private static final String CONNECTOR_ID = "custom:connectorId";

    @Value("${connector.response}")
    private String responseTopic;

    @Autowired
    private Connectors<? extends Connector<? extends ConnectorRequest<? extends ConnectorResponse>>> connectors;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private ConnectorCP beverCp;

    private static final TypeReference<HashMap<String, Object>> mapRef = new TypeReference<HashMap<String, Object>>() {
    };

    @KafkaListener(topics = "${connector.request}", groupId = "${spring.kafka.consumer.group-id}_sql_connector",
        errorHandler = "DataStoreError")
    public void processSQL(ConsumerRecord<String, Map<String, Object>> consumerRcd) throws JsonProcessingException {
        String type = getConnectorId();
        log.debug("Connector loaded with Id {}", type);
        Map<String, Object> consumerRecord = consumerRcd.value();
        String[] config = ((String) consumerRecord.get(CONNECTOR_ID)).split(":");
        String alternateId = config[0];
        String version = "";
        if (config.length > 1) {
            version = config[1];
        }
        String solutionId = (String) ((Map) consumerRecord.get("CONTEXT")).get("solutionId");
        beverCp.loadDS(alternateId, version, category, type, solutionId, false);
        Connector<? extends ConnectorRequest<?>> connector = connectors.getConnectorById(type);
        ConnectorRequest<?> request = connector.createRequest();
        request.setRequestParameters(consumerRecord);
        Map resp = new HashMap<>();
        try {
            ConnectorResponse response = request.execute();
            log.debug("Response after executing datastore is {}", response.getResponseParameters().get("response"));
            resp = objectMapper.readValue(response.getResponseParameters().get("response").toString(), mapRef);
            sendMessage(objectMapper.convertValue(resp, mapRef), consumerRcd.headers(), null);
        } catch (Exception e) {
            log.error("Exception Occured while processing kafka message", e);
            sendMessage(resp, consumerRcd.headers(), e);
        }
    }

    private String getConnectorId() {
        Optional<? extends Connector<? extends ConnectorRequest<? extends ConnectorResponse>>> findFirst = connectors
            .getAllAvailableConnectors().stream().filter((Connector connector) -> !"SCRIPT".equals(connector.getId())).findFirst();
        if (findFirst.isPresent()) {
            return findFirst.get().getId();
        } else {
            throw new IllegalArgumentException("Unable to load connectors");
        }
    }

    private void sendMessage(Map<String, Object> resp, Headers headers, Exception e) {
        ProducerRecord<String, Map> pd = new ProducerRecord<>(responseTopic, resp);
        if (Objects.nonNull(e)) {
            pd.headers().add(KafkaHeaders.EXCEPTION_MESSAGE, e.getMessage().getBytes());
            pd.headers().add("Status", "ERROR".getBytes());
            log.error("Exception occured while processing the datastore {} ", e);
        }
        headers.forEach(header -> pd.headers().add(header.key(), header.value()));
        kafkaTemplate.send(pd);
    }
}

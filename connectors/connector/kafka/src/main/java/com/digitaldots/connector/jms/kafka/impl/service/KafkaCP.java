/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.jms.kafka.impl.service;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.cache.Store;

@Component
public class KafkaCP implements Store<KafkaProducer<String, String>, KafkaProducer<String, String>> {

    @Override
    public KafkaProducer<String, String> getDataSource(Map<String, Object> properties) {
        Properties kafkaProperties = new Properties();
        kafkaProperties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, properties.get("bootStrapUrls"));
        kafkaProperties.compute(CommonClientConfigs.CONNECTIONS_MAX_IDLE_MS_CONFIG, (k, v) -> properties.get("conMaxIdleTime"));
        kafkaProperties.compute(CommonClientConfigs.DEFAULT_API_TIMEOUT_MS_CONFIG, (k, v) -> properties.get("requestTimeout"));
        kafkaProperties.compute(CommonClientConfigs.GROUP_ID_CONFIG, (k, v) -> properties.get("groupId"));
        kafkaProperties.compute(CommonClientConfigs.HEARTBEAT_INTERVAL_MS_CONFIG, (k, v) -> properties.get("heartBeat"));
        kafkaProperties.compute(CommonClientConfigs.MAX_POLL_INTERVAL_MS_CONFIG, (k, v) -> properties.get("maxPollingInterval"));
        kafkaProperties.compute(CommonClientConfigs.RECEIVE_BUFFER_CONFIG, (k, v) -> properties.get("receiveBufferSize"));
        kafkaProperties.compute(CommonClientConfigs.RECONNECT_BACKOFF_MAX_MS_CONFIG, (k, v) -> properties.get("retryBackoffTimeout"));
        kafkaProperties.compute(CommonClientConfigs.RETRIES_CONFIG, (k, v) -> properties.get("retries"));
        kafkaProperties.compute(CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG, (k, v) -> properties.get("retryBackoffTimeout"));
        kafkaProperties.putIfAbsent("transactional.id", "KAFKA");
        kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProperties.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(kafkaProperties);
        kafkaProducer.initTransactions();
        return kafkaProducer;
    }

    @Override
    public String getDBTye() {
        return "Kafka";
    }

    @Override
    public KafkaProducer<String, String> getConnection(KafkaProducer<String, String> datasource) {
        return datasource;
    }

}

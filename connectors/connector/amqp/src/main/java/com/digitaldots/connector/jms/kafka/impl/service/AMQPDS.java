/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.jms.kafka.impl.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.digitaldots.connector.cache.Store;
import com.rabbitmq.client.ConnectionFactory;

@Component
public class AMQPDS implements Store<ConnectionFactory, ConnectionFactory> {

    @Override
    public ConnectionFactory getDataSource(Map<String, Object> request) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(request.get("host").toString());
        factory.setPort(Integer.parseInt(request.getOrDefault("port", "-1").toString()));
        factory.setUsername(request.get("username").toString());
        factory.setPassword(request.get("password").toString());
        return factory;
    }

    @Override
    public String getDBTye() {
        return "AMQP";
    }

    @Override
    public ConnectionFactory getConnection(ConnectionFactory datasource) {
        return datasource;
    }

}

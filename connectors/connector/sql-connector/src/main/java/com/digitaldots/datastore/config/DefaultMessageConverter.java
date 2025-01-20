/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.datastore.config;

import java.lang.reflect.Type;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DefaultMessageConverter extends JsonMessageConverter {

    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> consumerRecord, Type type) {
        if (log.isDebugEnabled()) {
            log.debug("inside the message converteer >> {}", consumerRecord.topic());
        }
        Object value = null;
        try {
            value = super.extractAndConvertValue(consumerRecord, type);
            return value;
        } catch (Exception e) {
            log.error("Exception occured while transforming", e);
            value = consumerRecord.value();
            return value;
        }
    }

}
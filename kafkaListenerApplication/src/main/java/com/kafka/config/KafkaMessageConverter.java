package com.kafka.config;

import java.lang.reflect.Type;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaMessageConverter extends JsonMessageConverter {
    
    @Override
    public Object extractAndConvertValue(ConsumerRecord<?, ?> consumerRecord, Type type) {
        log.info("MC record type {}", consumerRecord.value().getClass().getName());
        return consumerRecord.value();
    }

}

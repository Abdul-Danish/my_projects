package com.digitaldots.function.filter;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RecordFilter implements RecordFilterStrategy<String, Object> {

    @Override
    public boolean filter(ConsumerRecord<String, Object> consumerRecord) {
        log.info("Message received from topic {} with record {}", consumerRecord.topic(), consumerRecord.value());
        log.info("Filtering Record");
        return false;
    }
}

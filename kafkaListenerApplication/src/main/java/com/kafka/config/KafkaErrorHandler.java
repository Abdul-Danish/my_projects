package com.kafka.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/*
 * Error Handler for handling Record Deserialization Error
 */
@Component
@Slf4j
public class KafkaErrorHandler implements CommonErrorHandler {

    @Override
    public boolean handleOne(Exception thrownException, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer,
        MessageListenerContainer container) {
        log.error("Exception intercepted by handle func");
        return handle(thrownException, consumer);
    }

    @Override
    public void handleOtherException(Exception exception, Consumer<?, ?> consumer, MessageListenerContainer container,
        boolean batchListener) {
        log.info("Exception intercepted by handle other func");
        handle(exception, consumer);
    }

    private boolean handle(Exception exception, Consumer<?, ?> consumer) {
        boolean handle = false;
        if (exception instanceof RecordDeserializationException ex) {
            log.info("inside handle");
            log.error("Exception occured while deserializing record: {}", consumer, exception);
            consumer.seek(ex.topicPartition(), ex.offset() + 1L);
            consumer.commitSync();
            handle = true;
        }
        return handle;
    }
}

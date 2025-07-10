package com.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KafkaConfig {

    @Bean("kafkaListenerContainerFactory")
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> concurrentKafkaListenerContainerFactory(
        ConsumerFactory<String, T> consumerFactory, KafkaMessageInterceptor<String, T> recordInterceptor,
        KafkaMessageConverter messageConverter) {
        ConcurrentKafkaListenerContainerFactory<String, T> listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setRecordInterceptor(recordInterceptor);
        listenerContainerFactory.setConsumerFactory(consumerFactory);
//        listenerContainerFactory.setRecordMessageConverter(messageConverter);
        listenerContainerFactory.setCommonErrorHandler(commonErrorHandler());
        listenerContainerFactory.setBatchListener(false);
        listenerContainerFactory.setAutoStartup(true);
        return listenerContainerFactory;
    }

    @Bean
    public <T> KafkaTemplate<String, T> kafkaTemplate(ProducerFactory<String, T> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Bean
    public <T> ProducerFactory<String, T> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    public <T> ConsumerFactory<String, T> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> consumerProps = kafkaProperties.buildConsumerProperties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        consumerProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

//    @Bean
//    public CommonErrorHandler commonErrorHandler() {
//        return new KafkaErrorHandler();
//    }

    @Bean
    public CommonErrorHandler commonErrorHandler() {
        log.info("creating commonErrorHandler");
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, e) -> {
            log.error(String.format("consumed record %s because this exception was thrown %s", consumerRecord.toString(),
                e.getClass().getName()));
        }, new FixedBackOff(30000, 9)) {
            @Override
            public void handleOtherException(Exception exception, Consumer<?, ?> consumer, MessageListenerContainer container,
                boolean batchListener) {
                log.info("Exception intercepted by handle other func");
                handleException(exception, consumer);
            }
        };
        errorHandler.addNotRetryableExceptions(NullPointerException.class);
        return errorHandler;
    }

    private void handleException(Exception exception, Consumer<?, ?> consumer) {
        if (exception instanceof RecordDeserializationException ex) {
            log.info("inside handle");
            log.error("Exception occured while deserializing record: {}", consumer, exception);
            consumer.seek(ex.topicPartition(), ex.offset() + 1L);
            consumer.commitSync();
        }
    }

//    @Bean
//    public CommonErrorHandler commonErrorHandler() {
//        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, error) -> {
//            log.error("Exception occured while deserializing record: {}", consumerRecord, error);
//        }, new FixedBackOff(3000, 1));
//        errorHandler.addNotRetryableExceptions(RecordDeserializationException.class);
//        errorHandler.addNotRetryableExceptions(SerializationException.class);
//        return errorHandler;
//    }

}

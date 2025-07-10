package com.digitaldots.function.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.adapter.FilteringMessageListenerAdapter;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.digitaldots.function.filter.RecordFilter;
import com.digitaldots.function.listeners.MessageDelegateListener;

@Configuration
public class KafkaConfig {

    @Bean
    public <K, V> ReplyingKafkaTemplate<String, K, V> replyingTemplate(ProducerFactory<String, K> pf,
        ConcurrentMessageListenerContainer<String, V> repliesContainer) {
        return new ReplyingKafkaTemplate<>(pf, repliesContainer);
    }

    @Bean
    public <T> ConcurrentMessageListenerContainer<String, T> repliesContainer(
        ConcurrentKafkaListenerContainerFactory<String, T> containerFactory) {
        ConcurrentMessageListenerContainer<String, T> repliesContainer = containerFactory.createContainer("kReplies");
        repliesContainer.getContainerProperties().setGroupId("repliesGroup");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean("kafkaListenerContainerFactory")
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> concurrentKafkaListenerContainerFactory(
        ConsumerFactory<String, T> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, T> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory);
        return concurrentKafkaListenerContainerFactory;
    }

    @Bean
    public <T> ProducerFactory<String, T> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.172.253:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    public <T> ConsumerFactory<String, T> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.172.253:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, BytesDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, BytesDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    
    @Bean
    public FilteringMessageListenerAdapter<String, Object> filteringMessageListenerAdapter(
        MessageDelegateListener delegateListener, RecordFilter recordFilter) {
        return new FilteringMessageListenerAdapter<>(delegateListener, recordFilter);
    }
    
}

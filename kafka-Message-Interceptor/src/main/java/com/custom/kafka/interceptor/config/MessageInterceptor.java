package com.custom.kafka.interceptor.config;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageInterceptor<K, V> implements ConsumerInterceptor<K, V> {

    Deserializer<?> deserializer;
    
    @Override
//    @SuppressWarnings("unchecked")
    public void configure(Map<String, ?> configs) {
        log.info("Updating Kafka config");
        
        String valueDeserializerClassName = (String) configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);
        
        // Check if the value deserializer class name is JsonDeserializer
        if (valueDeserializerClassName.equals(JsonDeserializer.class.getName())) {
            // Retrieve the existing JsonDeserializer from the configs
            JsonDeserializer<?> jsonDeserializer = (JsonDeserializer<?>) configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);
            
            // Add trusted packages to the JsonDeserializer
            jsonDeserializer.addTrustedPackages("org.json");
            
            // Update the deserializer reference with the modified JsonDeserializer
            this.deserializer = jsonDeserializer;
        } else {
            log.info("Value deserializer is not JsonDeserializer. Skipping configuration.");
        }
        
        /*
//        JsonDeserializer deserializer = new JsonDeserializer<>();
//        deserializer.addTrustedPackages("org.json");
        JsonDeserializer jsonDeserializer = (JsonDeserializer) configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);
        jsonDeserializer.addTrustedPackages("org.json");
//        Map<String, Object> mutableConfigs = (Map<String, Object>) configs;
//        mutableConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, jsonDeserializer);
//        mutableConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "JsonDeserializer.class");
//        mutableConfigs.put("JsonDeserializer.TRUSTED_PACKAGES", "org.json");
        
        this.deserializer = jsonDeserializer;
        */
    }

    @Override
    public ConsumerRecords<K, V> onConsume(ConsumerRecords<K, V> records) {
        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        
    }

    @Override
    public void close() {
        
    }

}

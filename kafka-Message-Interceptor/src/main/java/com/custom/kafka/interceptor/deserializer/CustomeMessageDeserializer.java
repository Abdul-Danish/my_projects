package com.custom.kafka.interceptor.deserializer;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomeMessageDeserializer<T> implements Deserializer<T> {

    ObjectMapper objectMapper = new ObjectMapper();
    
    private final Class<T> targetType;

    public CustomeMessageDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }
    
    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, targetType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    
}

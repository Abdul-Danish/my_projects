package com.digitaldots.function.listeners;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

@Component
public class ReceiveTaskListener {
    private static final String STATUS = "status";
    
    @Autowired
    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

//    @KafkaListener(topics = "PROCESS", groupId = "test", containerFactory = "kafkaListenerContainerFactory")
    public void listener(ConsumerRecord<String, Object> consumerRecord) {
        Headers kafkaHeaders = consumerRecord.headers();
        String solutionId = new String(kafkaHeaders.lastHeader("solutionId").value());
        String correlationId = new String(kafkaHeaders.lastHeader(KafkaHeaders.CORRELATION_ID).value());
        Header responseTopicHeader = kafkaHeaders.lastHeader("ResponseTopic");
        Header replyTopicHeader = kafkaHeaders.lastHeader(KafkaHeaders.REPLY_TOPIC);
        String responseTopic = null;
        String replyTopic = null;
        if (responseTopicHeader!= null) {
            responseTopic = new String(responseTopicHeader.value());
        }
        if (replyTopicHeader != null) {
            replyTopic = new String(replyTopicHeader.value());
        }
        System.out.println("Headers Data: " + solutionId + " " + correlationId + " " + responseTopic + " " + replyTopic);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", "Connector Executed Successfully");
        Map<String, Object> context = new HashMap<>();
        context.put("solutionId", solutionId);

        System.out.println("Message Sent");
        ProducerRecord<String, Map<String, Object>> producerRecord = new ProducerRecord<String, Map<String, Object>>(
            "PROCESS.RESPONSE", payload);
//        producerRecord.headers().add(KafkaHeaders.REPLY_TOPIC, "676d3bdfd7706c4aecd109b9.func.response".getBytes());
        producerRecord.headers().add(KafkaHeaders.CORRELATION_ID, correlationId.getBytes());
        producerRecord.headers().add("solutionId", solutionId.getBytes());
        producerRecord.headers().add("Authorization",
            "eyJhbGciOiJIUzUxMiJ9.eyJmaXJzdE5hbWUiOiJhYmR1bCIsImxhc3ROYW1lIjoiZGFuaXNoIiwiZmlyc3RUaW1lTG9naW4iOmZhbHNlLCJyb2xlcyI6WyJBZG1pbiJdLCJncm91cHMiOltdLCJpZCI6IjY3YmM1NjhmOWNiYTRjNzUyNDgxMWZhYSIsInJlZnJlc2hUb2tlbiI6IjNhMzU1MmFjLTA5NDctNDUwOC04ZjhiLTI3MzlhNTJjNTJhZSIsInN1YiI6ImRhbmlzaEBkaWdpdGFsZG90cy5haSIsImlhdCI6MTc0MjI5MDY5NiwiZXhwIjoxNzQyMjk0Mjk2fQ.EtlO-Q-lNZ6eVDh_dRpUX36r3dtynwAXFBKS6YIDazdDedLlvb4RBLyF6xxUu3ZD9RgFqncaIECaMuTwDgpFqA"
                .getBytes());
        producerRecord.headers().add(STATUS, "COMPLETED".getBytes());
        producerRecord.headers().add("filter", "STEP1".getBytes());
        kafkaTemplate.send(producerRecord);
    }
    
}

package com.digitaldots.function.listeners;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.digitaldots.function.model.Student;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StudentListener {
    
//    @KafkaListener(topics = { "STUDENT.TOPIC" }, groupId = "student_group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, Student> studentRecord) {
        log.info("Received Record from topic {}", studentRecord.topic());
    }

}

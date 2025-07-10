package com.kafka.listeners;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.kafka.model.Student;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StudentListener {
    
    @KafkaListener(topics = { "STUDENT.TOPIC" }, groupId = "student_group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(Student studentRecord) {
      log.info("Received Record from topic: STUDENT.TOPIC");
      log.info("Record {}", studentRecord);
        
//        log.info("Received Record from topic {}", studentRecord.topic());
//        log.info("Record {}", studentRecord.value());
    }

}

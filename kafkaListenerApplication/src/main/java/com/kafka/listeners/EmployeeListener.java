package com.kafka.listeners;

import org.models.Employee;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmployeeListener {

    @KafkaListener(topics = { "EMPLOYEE.TOPIC" }, groupId = "employee_group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload Employee employeeRecord) {
        log.info("Received Record from topic: EMPLOYEE.TOPIC");
        log.info("Record {}", employeeRecord);

//        log.info("Received Record from topic {}", employeeRecord.topic());
//        log.info("Record {}", employeeRecord.value());
    }

}

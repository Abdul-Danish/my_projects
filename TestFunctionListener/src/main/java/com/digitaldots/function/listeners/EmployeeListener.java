package com.digitaldots.function.listeners;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.digitaldots.function.model.Employee;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmployeeListener {

//    @KafkaListener(topics = { "EMPLOYEE.TOPIC" }, groupId = "employee_group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, Employee> employeeRecord) {
        log.info("Received Record from topic {}", employeeRecord.topic());
    }

}

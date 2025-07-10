package com.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    @Autowired
    private KafkaTemplate<String, Employee> kafkaTemplateEmp;
    
    @GetMapping("/send")
    public void send() {
        Employee employee = Employee.builder().id("100").empName("emp1").build();
        ProducerRecord<String, Employee> producerRecordEmp = new ProducerRecord<>("EMPLOYEE.TOPIC", employee);
        kafkaTemplateEmp.send(producerRecordEmp);
    }
    
}

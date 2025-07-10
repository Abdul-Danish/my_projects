package com.producer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.models.Employee;
import org.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;

@SpringBootApplication
public class KafkaProducerApplication implements CommandLineRunner {
    private static final String STATUS = "status";

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerApplication.class, args);
    }

    @Autowired
    private KafkaTemplate<String, Employee> kafkaTemplateEmp;

    @Autowired
    private KafkaTemplate<String, Student> kafkaTemplateStudent;

    @Autowired
    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
//        Student student = Student.builder().id("200").studentName("studet1").build();
//        ProducerRecord<String, Student> producerRecordEmp = new ProducerRecord<>("EMPLOYEE.TOPIC", student);
//        kafkaTemplateStudent.send(producerRecordEmp);

        Employee employee = Employee.builder().id("100").empName("emp1").build();
        ProducerRecord<String, Employee> producerRecordEmp = new ProducerRecord<>("EMPLOYEE.TOPIC", employee);
        kafkaTemplateEmp.send(producerRecordEmp);
    }

//    @Override
    public void runTmp(String... args) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", "Connector Executed Successfully");
        Map<String, Object> context = new HashMap<>();
        context.put("solutionId", "676d3bdfd7706c4aecd109b9");

        System.out.println("Message Sent");
        ProducerRecord<String, Map<String, Object>> producerRecord = new ProducerRecord<String, Map<String, Object>>(
            "MODEL.VISION.RESPONSE", payload);
        producerRecord.headers().add(KafkaHeaders.CORRELATION_ID, "135c93fe-846a-4603-a9b3-70e7354ef696".getBytes());
        producerRecord.headers().add("solutionId", "67f8da8b5987390ca0203099".getBytes());
        producerRecord.headers().add("Authorization",
            "eyJhbGciOiJIUzUxMiJ9.eyJmaXJzdE5hbWUiOiJhYmR1bCIsImxhc3ROYW1lIjoiZGFuaXNoIiwiZmlyc3RUaW1lTG9naW4iOmZhbHNlLCJyb2xlcyI6WyJTdXBlciBVc2VyIl0sImdyb3VwcyI6W10sImlkIjoiNjdmNzg4YTRkNDhhYjk2OGVjOTdmZDNjIiwicmVmcmVzaFRva2VuIjoiZTZlMTkzZWUtYmFhNy00MmJhLWJiZmEtMmNmNjY0MWQ1MzNjIiwic3ViIjoiZGFuaXNoQGRpZ2l0YWxkb3RzLmFpIiwiaWF0IjoxNzUwMDcxMTc5LCJleHAiOjE3NTAwNzQ3Nzl9.c39eKWJ73DrgAdspoEK536zWcjEmFnl50ZlTIdv7L2ASAWSHtICGNzBfKUGy1WqPfPcTU_QspR-Yqn-zqEJsAA"
                .getBytes());
        producerRecord.headers().add("tenantId", "sandbox".getBytes());
//        producerRecord.headers().add(KafkaHeaders.REPLY_TOPIC, "676d3bdfd7706c4aecd109b9.func.response".getBytes());
//        producerRecord.headers().add("processId", "pd1a7n4i6s6h11305717".getBytes());
//        producerRecord.headers().add("activityId", "Activity_019g5ti".getBytes());
        producerRecord.headers().add(STATUS, "SUCCESS".getBytes());
        kafkaTemplate.send(producerRecord);
    }

}

package com.digitaldots.function;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.adapter.FilteringMessageListenerAdapter;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.digitaldots.function.filter.RecordFilter;
import com.digitaldots.function.model.Employee;
import com.digitaldots.function.model.Student;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class TestFunctionListenerApplication implements CommandLineRunner {

    private static final String STATUS = "status";

    private static final String CONNECTOR_ID = "custom:connectorId";

    public static void main(String[] args) {
        SpringApplication.run(TestFunctionListenerApplication.class, args);
    }

    @Autowired
    private ReplyingKafkaTemplate<String, Map<String, Object>, Map<String, Object>> replyingKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private KafkaTemplate<String, Employee> kafkaTemplateEmp;
    
    @Autowired
    private KafkaTemplate<String, Student> kafkaTemplateStudent;
    
//    @Autowired
//    private MsgListener msgListener;

    @Autowired
    private RecordFilter recordFilter;
    
    @Autowired
    private FilteringMessageListenerAdapter filteringMessageListenerAdapter;

//    @Bean
//    public RecordFilterStrategy<String, Object> recordFilter() {
//        return new RecordFilter();
//    }
    
//    @Override
    public void runFilter(String... args) throws Exception {
        Map<String, Object> consumerProperties = new HashMap<>();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        DefaultKafkaConsumerFactory<Object, Object> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);
        
        ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(defaultKafkaConsumerFactory);        
//        containerFactory.setAckDiscarded(true);
        ConcurrentMessageListenerContainer<String, Object> listenerContainer = containerFactory.createContainer("PROCESS");
//        listenerContainer.setupMessageListener(new FilteringMsgListener(new MessageDelegateListener(), new RecordFilter()));
        listenerContainer.setupMessageListener(filteringMessageListenerAdapter);
        listenerContainer.start();
        log.info("Container Status is: {}", listenerContainer.isRunning());
        
        Thread.sleep(5000);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", "Connector Executed Successfully");
        ProducerRecord<String, Map<String, Object>> producerRecord = new ProducerRecord<String, Map<String, Object>>("PROCESS", payload);
//        kafkaTemplate.send(producerRecord);
        log.info("Message Sent");
    }
    
//    @Override
    public void runTmp(String...  args) throws Exception {
        Employee employee = Employee.builder().id("100").empName("emp1").build();
        ProducerRecord<String, Employee> producerRecordEmp = new ProducerRecord<>("EMPLOYEE.TOPIC", employee);
        kafkaTemplateEmp.send(producerRecordEmp);
        log.info("Sent to topic");
        
//        Student student = Student.builder().id("200").studentName("student1").build();
//        ProducerRecord<String, Student> producerRecordStu = new ProducerRecord<>("EMPLOYEE.TOPIC", student);
//        kafkaTemplateStudent.send(producerRecordStu);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Default Key Serializer: " + kafkaTemplate.getProducerFactory().getKeySerializer());
        System.out.println("Default value Serializer: " + kafkaTemplate.getProducerFactory().getValueSerializer());
//        String topicName = "MODEL.VISION.RESPONSE";
        String topicName = "sandbox_MODEL.RESPONSE";
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", "Connector Executed Successfully");
        Map<String, Object> context = new HashMap<>();
        context.put("solutionId", "676d3bdfd7706c4aecd109b9");

        System.out.println("Message Sent");
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<String, Object>(
            topicName, payload.toString());
//        producerRecord.headers().add(KafkaHeaders.REPLY_TOPIC, "676d3bdfd7706c4aecd109b9.func.response".getBytes());
        producerRecord.headers().add(KafkaHeaders.CORRELATION_ID, "135c93fe-846a-4603-a9b3-70e7354ef696".getBytes());
        producerRecord.headers().add("solutionId", "67f8da8b5987390ca0203099".getBytes());
        producerRecord.headers().add("Authorization",
            "eyJhbGciOiJSUzI1NiIsImtpZCI6InNvNEdXVHdUZGRWOXNSRHVoU0tfWk5uSUVCRjBNS2hPc2JYV2p1MDVwQTAiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiI5NmVmZDkzNC03Yjc3LTQ0ZWItYWE4OC00MDYyNThmYzhhZDYiLCJpc3MiOiJodHRwczovL3JhcHBzaW5kdXMuYjJjbG9naW4uY29tLzFhZTA3NzRkLWJjOGUtNDgxMy05MGRkLTFlOGNlODJjNzQ5NS92Mi4wLyIsImV4cCI6MTc1MTk2NTU3NiwibmJmIjoxNzUxOTYxOTc2LCJvaWQiOiIyYzlmN2Q1Yi02MWFmLTQ5YmYtODJjYi05ZGMzMTBmYWJlMzAiLCJuYW1lIjoiQWJkdWwgRGFuaXNoIiwiZW1haWwiOiJkYW5pc2hAZGlnaXRhbGRvdHMuYWkiLCJzdWIiOiJkYW5pc2hAZGlnaXRhbGRvdHMuYWkiLCJ0aWQiOiIxYWUwNzc0ZC1iYzhlLTQ4MTMtOTBkZC0xZThjZTgyYzc0OTUiLCJub25jZSI6IjAxOTdlOTExLWVlZTEtN2UwYS1hZTMzLWQ0MjAyMGE5NTExNSIsImF6cCI6Ijk2ZWZkOTM0LTdiNzctNDRlYi1hYTg4LTQwNjI1OGZjOGFkNiIsInZlciI6IjEuMCIsImlhdCI6MTc1MTk2MTk3Nn0.n06Bv12yx5dl0hioBKsZE5VrUsItfgdBU03w9vEUdJalu_dGBBbiC3BbDD1FbGaSLjFYJQ1cWJLp5LvWHrzGpUs-N3_eP9Zk4cpZFhfLxQphNVggz_GcW4x_Hm2aR3VySsTRcZMON8-oxpD5-tJhSxFiZ3aAVCew9CmmnjKvNOo5Zhi8VX7K0p6Hz0LucJDmq6bK5aJaPvrMhZw4cSPzIzsno7HkakLSWhKBr43v_Fu7WgE0wCBSpl334EF27-Cvu7sbwlw15PwaXNlWL5Wl6aw3biT8XABHrH3SdTv-oYsnXQE36u62pOaAM4PybtgGy1nlzifgCSVFy1R_t1H1qA"
                .getBytes());
        producerRecord.headers().add("tenantId", "sandbox".getBytes());
        producerRecord.headers().add("processId", "pd1a7n4i6s6h11305717".getBytes());
        producerRecord.headers().add("activityId", "Activity_019g5ti".getBytes());
        producerRecord.headers().add(STATUS, "SUCCESS".getBytes());
//        producerRecord.headers().add("filter", "STEP1".getBytes());
        kafkaTemplate.send(producerRecord);

        /*
         * Map<String, Object> request = new HashMap<>(); Map<String, Object> payload = new HashMap<>(); payload.put("content",
         * "Connector Executed Successfully"); Map<String, Object> context = new HashMap<>(); context.put("solutionId",
         * "676d3bdfd7706c4aecd109b9");
         * 
         * request.put("request", payload); request.put(CONNECTOR_ID, "123"); request.put("CONTEXT", context); request.put("headers", null);
         * 
         * System.out.println("Message Sent"); ProducerRecord<String, Map<String, Object>> producerRecord = new ProducerRecord<String,
         * Map<String, Object>>( "676d3bdfd7706c4aecd109b9.func", request); producerRecord.headers().add(KafkaHeaders.REPLY_TOPIC,
         * "676d3bdfd7706c4aecd109b9.func.response".getBytes()); Map res =
         * replyingKafkaTemplate.sendAndReceive(producerRecord).get().value();
         * 
         * System.out.println("Response is: " + res);
         */
    }

}

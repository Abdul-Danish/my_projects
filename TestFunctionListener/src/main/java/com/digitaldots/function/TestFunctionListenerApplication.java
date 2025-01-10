package com.digitaldots.function;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;

@SpringBootApplication
public class TestFunctionListenerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TestFunctionListenerApplication.class, args);
	}

	@Autowired
	private ReplyingKafkaTemplate<String, Map<String, Object>, Map<String, Object>> replyingKafkaTemplate;
	
    @Override
    public void run(String... args) throws Exception {
        
        Map<String, Object> request = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", "Connector Executed Successfully");
        
        request.put("request", payload);
        request.put("headers", null);
        
        System.out.println("Message Sent");
        ProducerRecord<String, Map<String, Object>> producerRecord = new ProducerRecord<String, Map<String,Object>>(
            String.join(".", "676d3bdfd7706c4aecd109b9", "func"), request);
        producerRecord.headers().add(KafkaHeaders.REPLY_TOPIC,
            String.join(".", "676d3bdfd7706c4aecd109b9", "func", "response").getBytes());
        Map res = replyingKafkaTemplate.sendAndReceive(producerRecord).get().value();
        
        System.out.println("Response is: " + res);
    }

}

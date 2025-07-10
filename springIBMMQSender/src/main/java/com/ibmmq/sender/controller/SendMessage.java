package com.ibmmq.sender.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.models.Parameter;
import org.models.ProcessTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibmmq.sender.config.PostProcessor;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.Topic;

@RestController
@RequestMapping("/api/v1")
public class SendMessage {
    
    @Autowired
    private MessageConverter messageConverter;
    
    @Autowired
    @Qualifier("userCredentialsConnectionFactoryAdapter")
    private ConnectionFactory connectionFactory;

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody ProcessTrigger processTrigger) throws JMSException {
        Map<String, String> response = new HashMap<>();
        
//        Connection connection = connectionFactory.createConnection();
//        Session session = connection.createSession();
//        Topic topic = session.createTopic("TEST");
//        System.out.println("Created Topic: " + topic.getTopicName());
        
        List<Parameter> inputParams = processTrigger.getIo().get("input");
        Map<String, Object> headers = processTrigger.getProperties();
        String topicName = getResponseListenerTopic(inputParams);
        System.out.println("Sending to Topic: " + topicName);
        System.out.println("headers: " + headers);
        System.out.println("pub-sub: " + jmsTemplate.isPubSubDomain());
        jmsTemplate.convertAndSend(topicName, processTrigger, new PostProcessor(headers));
        
        response.put("response", "sent");
        return ResponseEntity.ok(response);
    }
    
    private String getResponseListenerTopic(List<Parameter> inputParams) {
        for (Parameter param : inputParams) {
            if ("responseTopic".equals(param.getName()) || "ResponseTopic".equals(param.getName())) {
                return param.getValue();
            }
        }
        return null;
    }

}

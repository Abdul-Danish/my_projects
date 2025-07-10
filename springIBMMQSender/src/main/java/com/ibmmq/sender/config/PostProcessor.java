package com.ibmmq.sender.config;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jms.core.MessagePostProcessor;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.extern.slf4j.Slf4j;

public class PostProcessor implements MessagePostProcessor {
    
    public Map<String, Object> headers;

    public PostProcessor(Map<String, Object> headers) {
        this.headers = headers;
    }
    
    @Override
    public Message postProcessMessage(Message message) throws JMSException {
            for (Entry<String, Object> header : headers.entrySet()) {
                    System.out.println("Adding property: " + header.getKey() + " " + header.getValue());
                    message.setObjectProperty(header.getKey().replace("filter_", ""), header.getValue());
            }
        System.out.println("end: " + message);
        return message;
    }

}

package com.ibmmq.receiver.listener;

import java.util.Map;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Receiver {

//    @JmsListener(destination = "DEV.QUEUE.2", containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(Map<String, String> msg) {
        log.info("Message Received: {}", msg);
        System.out.println("Received <" + msg + ">");
    }

}

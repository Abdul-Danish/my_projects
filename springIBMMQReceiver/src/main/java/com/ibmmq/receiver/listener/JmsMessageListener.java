package com.ibmmq.receiver.listener;

import org.springframework.context.annotation.Scope;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import com.ibmmq.receiver.model.MessageListener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

@Component
@Scope("prototype")
public class JmsMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        System.out.println("Listening to 'ON-MESSAGE' Event");
        try {
            System.out.println("Message: " + message);
            System.out.println();
            if (message instanceof TextMessage) {
                TextMessage textMessage = ((TextMessage) message);
                String text = textMessage.getText();
                System.out.println("Payload: " + text);
            } else {
                Object obj = message.getBody(Object.class);
                System.out.println("Payload (obj): " + obj);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}

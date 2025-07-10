package com.ibmmq.receiver.config;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;

public class JmsInterceptor implements MessageListener {

    private com.ibmmq.receiver.model.MessageListener delegate;
    
    public JmsInterceptor(com.ibmmq.receiver.model.MessageListener delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void onMessage(Message message) {
        System.out.println("Intercepted message");       
        delegate.onMessage(message);
    }

}

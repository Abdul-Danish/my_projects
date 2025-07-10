package com.ibmmq.receiver.config;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.Topic;

//@Configuration
public class JmsConfig {
    
//    @Bean
    public JmsTemplate jmsTemplate(UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter) {
        JmsTemplate jmsTemplate = new JmsTemplate(userCredentialsConnectionFactoryAdapter);
        return jmsTemplate;
    }

//    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory,
        DefaultJmsListenerContainerFactoryConfigurer configurer, MessageConverter jacksonJmsMessageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all auto-configured defaults to this factory, including the message converter
        factory.setMessageConverter(jacksonJmsMessageConverter);
        configurer.configure(factory, connectionFactory);
        // You could still override some settings if necessary.
        return factory;
    }
    
//    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
      MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
      converter.setTargetType(MessageType.TEXT);
      converter.setTypeIdPropertyName("_type");
      return converter;
    }
    
//    @Bean
    public MQQueueConnectionFactory mqConnectionFactory() throws JMSException {
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
//        mqQueueConnectionFactory.setConnectionNameList(channel);
        
        mqQueueConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
//        mqQueueConnectionFactory.setCCSID(1200);
        mqQueueConnectionFactory.setChannel("DEV.APP.SVRCONN");
        mqQueueConnectionFactory.setHostName("localhost");
        mqQueueConnectionFactory.setPort(1414);
        mqQueueConnectionFactory.setQueueManager("QM1");
        
        return mqQueueConnectionFactory;
    }
    
//    @Bean
    public Topic createTopic(ConnectionFactory connectionFactory) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession();
        session.createQueue("QM1");
        return session.createTopic("TEST");
    }
    
}

package com.ibmmq.sender.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;

@Configuration
public class JmsConfig {

    @Value("${ibm.mq.queueManager}")
    private String queueManager;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.connName}")
    private String connection;

    @Value("${ibm.mq.user}")
    private String user;

    @Value("${ibm.mq.password}")
    private String password;

    @Bean
    public JmsListenerContainerFactory<?> listenerContainerFactory(
        @Qualifier("userCredentialsConnectionFactoryAdapter") ConnectionFactory connectionFactory,
        DefaultJmsListenerContainerFactoryConfigurer configurer, MessageConverter jacksonJmsMessageConverter) throws JMSException {
//      Connection connection = connectionFactory.createConnection("app", "9966667462");        
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all auto-configured defaults to this factory, including the message converter
        factory.setMessageConverter(jacksonJmsMessageConverter);
        configurer.configure(factory, connectionFactory);
        // You could still override some setstings if necessary.
        return factory;
    }

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

//    @Bean
//    public Topic createTopic(@Qualifier("mqConnectionFactory") ConnectionFactory connectionFactory) throws JMSException {
//        Connection connection = connectionFactory.createConnection("app", "9966667462");
//        Session session = connection.createSession();
//        return session.createTopic("TEST");
//    }

    @Bean
    public MQConnectionFactory mqConnectionFactory() throws JMSException {
        MQConnectionFactory mqQueueConnectionFactory = new MQConnectionFactory();
        mqQueueConnectionFactory.setChannel(channel);
        mqQueueConnectionFactory.setConnectionNameList(connection);
        mqQueueConnectionFactory.setQueueManager(queueManager);
        mqQueueConnectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        return mqQueueConnectionFactory;
    }

    @Bean
    public UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(
        MQConnectionFactory mqQueueConnectionFactory) {
        UserCredentialsConnectionFactoryAdapter credentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
        credentialsConnectionFactoryAdapter.setUsername(user);
        credentialsConnectionFactoryAdapter.setPassword(password);
        credentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqQueueConnectionFactory);
        return credentialsConnectionFactoryAdapter;
    }

    @Bean
    public JmsTemplate jmsTemplate(UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter) {
        JmsTemplate jmsTemplate = new JmsTemplate(userCredentialsConnectionFactoryAdapter);
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

}

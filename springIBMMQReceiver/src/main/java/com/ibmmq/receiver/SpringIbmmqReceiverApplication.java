package com.ibmmq.receiver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.util.backoff.FixedBackOff;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import com.ibm.msg.client.jakarta.wmq.compat.jms.internal.JMSC;
import com.ibmmq.receiver.config.JmsInterceptor;
import com.ibmmq.receiver.listener.JmsMessageListener;
import com.ibmmq.receiver.model.JmsFilter;
import com.ibmmq.receiver.model.JmsOperator;

import jakarta.jms.JMSException;

@SpringBootApplication
@EnableJms
public class SpringIbmmqReceiverApplication {
//implements CommandLineRunner {
    
    private static final String AND = "AND";

    public static void main(String[] args) {
        SpringApplication.run(SpringIbmmqReceiverApplication.class, args);
    }

    @Autowired
    private JmsMessageListener jmsMessageListener;
    
//    @Override
    public void run(String... args) throws Exception {
        List<JmsFilter> filters = getFilters();
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        configureContainerProperties(container);
        container.setMessageConverter(getJacksonJmsMessageConverter());
        container.setConnectionFactory(getUserCredentialsConnectionFactory());
        container.setDestinationName("DEV.QUEUE.2");
        // Add filter (selector)
        String contructFilter = contructFilter(filters);
        container.setMessageSelector(contructFilter);
        container.setupMessageListener(new JmsInterceptor(jmsMessageListener));
        container.afterPropertiesSet();
        container.start();
        String logMessage = String.format("Started Jms Container: %s with status %s", container.getDestinationName(),
            container.isRunning());
        System.out.println(logMessage);
    }

    private MQQueueConnectionFactory getConnectionFactory() throws JMSException {
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
//        mqQueueConnectionFactory.setChannel("DEV.APP.SVRCONN");
//        mqQueueConnectionFactory.setHostName("localhost");
//        mqQueueConnectionFactory.setPort(1414);
//        mqQueueConnectionFactory.setQueueManager("QM1");
        mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
        configureConnectionProperties(mqQueueConnectionFactory);
        return mqQueueConnectionFactory;
    }

    private UserCredentialsConnectionFactoryAdapter getUserCredentialsConnectionFactory() throws JMSException {
        UserCredentialsConnectionFactoryAdapter credentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
        credentialsConnectionFactoryAdapter.setUsername("app");
        credentialsConnectionFactoryAdapter.setPassword("9966667462");
        credentialsConnectionFactoryAdapter.setTargetConnectionFactory(getConnectionFactory());
        return credentialsConnectionFactoryAdapter;
    }
    
    private DefaultMessageListenerContainer configureContainerProperties(DefaultMessageListenerContainer container) {
        container.setBackOff(new FixedBackOff(FixedBackOff.DEFAULT_INTERVAL, 3l));
        container.setAutoStartup(false);
        // container.setMaxMessagesPerTask(0); //Limits how many messages a single task (thread) will process before it's released and a new one is created.
        // container.setErrorHandler(null);
         container.setReceiveTimeout(1000); // How long the listener waits for a message before giving up. (max.poll.interval.ms)
        // container.setRecoveryInterval(0); // not required with backoff, Controls how long to wait between reconnection attempts (reconnect.backoff.max.ms)
         container.setPubSubDomain(false); 
         // Point-to-Point (Queues): Only one receiver gets the message, Publish-Subscribe (Topic): All active subscribers get a copy
        container.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER); //can’t change the MessageSelector at runtime without restarting.
        return container;
    }
    
    private MQQueueConnectionFactory configureConnectionProperties(MQQueueConnectionFactory connectionFactory) throws JMSException {
        connectionFactory.setStringProperty(WMQConstants.WMQ_CONNECTION_NAME_LIST, "localhost(1414)");
        connectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, "DEV.APP.SVRCONN");
        connectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, "QM1");
        connectionFactory.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_OPTIONS, WMQConstants.WMQ_CLIENT_RECONNECT);
        connectionFactory.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_TIMEOUT, 60);
//        connectionFactory.setObjectProperty(WMQConstants.WMQ_MAX_BUFFER_SIZE, 65536);
        connectionFactory.setObjectProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
        return connectionFactory;
    }
    
    private MessageConverter getJacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
      }

    private String contructFilter(List<JmsFilter> filters) {
        StringBuffer buffer = new StringBuffer();
        for (JmsFilter filter : filters) {
            String filterValue = filter.getValue();
            buffer
            .append(filter.getKey())
            .append(" ")
            .append(filter.getOperator().getSymbol())
            .append(" ");
            if (filterValue instanceof String && !isBoolean(filterValue) && !isNumeric(filterValue)) {
                System.out.println("String instance: " + filterValue);
                buffer.append("'").append(filterValue).append("'");
            } else if (isBoolean(filterValue)) {
                System.out.println("Boolean Instance: " + filterValue);
                buffer.append(filterValue.toLowerCase());
            } else if (isNumeric(filterValue)) {
                System.out.println("Number Instance: " + filterValue);
                buffer.append(filterValue);
            } else {
                System.out.println("Invalid Type");
            }
            buffer.append(" ")
            .append(AND)
            .append(" ");
        }
        String filterString = buffer.substring(0, buffer.length() - 5);
        System.out.println("Constructed filter: " + filterString);
        return filterString;
    }
    
    private List<JmsFilter> getFilters() {
        List<JmsFilter> jmsFilters = new ArrayList<>();
        JmsFilter filter1 = JmsFilter.builder().key("STATUS").value("SUCCESS").operator(JmsOperator.EQ).build();
        JmsFilter filter2 = JmsFilter.builder().key("PROCEED").value("True").operator(JmsOperator.EQ).build();
        JmsFilter filter3 = JmsFilter.builder().key("PRIORITY").value("5").operator(JmsOperator.GT).build();
        
        jmsFilters.add(filter1);
        jmsFilters.add(filter2);
        jmsFilters.add(filter3);
        return jmsFilters;
    }
    
    private boolean isBoolean(String val) {
        return ("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val));
    }
    
    private boolean isNumeric(String val) {
        try {
            Integer.parseInt(val);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
}

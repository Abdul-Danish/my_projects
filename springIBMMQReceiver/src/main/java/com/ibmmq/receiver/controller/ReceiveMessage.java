package com.ibmmq.receiver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.models.Parameter;
import org.models.ProcessTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import com.ibm.msg.client.jakarta.wmq.compat.jms.internal.JMSC;
import com.ibmmq.receiver.config.JmsInterceptor;
import com.ibmmq.receiver.listener.JmsMessageListener;

import jakarta.jms.JMSException;

@RestController
@RequestMapping("/api/v1")
public class ReceiveMessage {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsMessageListener jmsMessageListener;

    @Value("${ibm.mq.user}")
    private String user;

    @Value("${ibm.mq.password}")
    private String password;

    @PostMapping("/create/container")
    public void createContainer(@RequestBody ProcessTrigger processTrigger) throws JMSException {
        List<Parameter> inputParams = processTrigger.getIo().get("input");
        System.out.println("Input Params: " + inputParams);
        String topicName = getResponseListenerTopic(inputParams);
        System.out.println("Topic Name: " + topicName);
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        MQConnectionFactory connectionFactory = getConnectionFactory(processTrigger);
        container.setConnectionFactory(getUserCredentialsConnectionFactory(connectionFactory));
        configureContainerProperties(container, topicName);
        container.setMessageSelector(createFilter(inputParams));

        container.setupMessageListener(new JmsInterceptor(jmsMessageListener));
        container.setPubSubDomain(true);
        container.afterPropertiesSet();
        container.start();
        String logMessage = String.format("Started Jms Container: %s with status %s", container.getDestinationName(),
            container.isRunning());
        System.out.println(logMessage);
    }

    private String getResponseListenerTopic(List<Parameter> inputParams) {
        for (Parameter param : inputParams) {
            if ("responseTopic".equals(param.getName()) || "ResponseTopic".equals(param.getName())) {
                return param.getValue();
            }
        }
        return null;
    }

    private DefaultMessageListenerContainer configureContainerProperties(DefaultMessageListenerContainer container, String topicName) {
        container.setDestinationName(topicName);
        container.setBackOff(new FixedBackOff(FixedBackOff.DEFAULT_INTERVAL, 3l));
        container.setAutoStartup(false);
        // container.setMaxMessagesPerTask(0); //Limits how many messages a single task (thread) will process before it's released and a new
        // one is created.
        // container.setErrorHandler(null);
        container.setReceiveTimeout(1000); // How long the listener waits for a message before giving up. (max.poll.interval.ms)
        // container.setRecoveryInterval(0); // not required with backoff, Controls how long to wait between reconnection attempts
        // (reconnect.backoff.max.ms)
        container.setPubSubDomain(false);
        // Point-to-Point (Queues): Only one receiver gets the message, Publish-Subscribe (Topic): All active subscribers get a copy
        container.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER); // can’t change the MessageSelector at runtime without
                                                                                 // restarting.
        return container;
    }

    private MQConnectionFactory getConnectionFactory(ProcessTrigger processTrigger) throws JMSException {
        Map<String, Object> properties = processTrigger.getProperties();
        System.out.println("Properties: " + properties);

        MQConnectionFactory connectionFactory = new MQConnectionFactory();
        connectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
        connectionFactory.setObjectProperty(WMQConstants.WMQ_CONNECTION_NAME_LIST, properties.get("connection_name"));
        connectionFactory.setObjectProperty(WMQConstants.WMQ_CHANNEL, properties.get("channel"));
        connectionFactory.setObjectProperty(WMQConstants.WMQ_QUEUE_MANAGER, properties.get("queue_manager"));
        connectionFactory.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_OPTIONS, WMQConstants.WMQ_CLIENT_RECONNECT);
        connectionFactory.setObjectProperty(WMQConstants.WMQ_CLIENT_RECONNECT_TIMEOUT, properties.get("reconnect_timeout"));
        connectionFactory.setObjectProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
        return connectionFactory;
    }

    private UserCredentialsConnectionFactoryAdapter getUserCredentialsConnectionFactory(MQConnectionFactory connectionFactory)
        throws JMSException {
        UserCredentialsConnectionFactoryAdapter credentialsConnectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
        credentialsConnectionFactoryAdapter.setUsername(user);
        credentialsConnectionFactoryAdapter.setPassword(password);
        credentialsConnectionFactoryAdapter.setTargetConnectionFactory(connectionFactory);
        return credentialsConnectionFactoryAdapter;
    }

    private String createFilter(List<Parameter> inputParams) {
        Parameter filterParam = Objects.isNull(inputParams) ? null
            : inputParams.stream().filter(param -> "filter".equals(param.getName())).findAny().orElse(null);
        System.out.println("Filter Param is: " + filterParam);
        if (Objects.isNull(filterParam) || Objects.isNull(filterParam.getMap()) || filterParam.getMap().isEmpty()) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, String> filterItem : filterParam.getMap().entrySet()) {
            String filterValue = filterItem.getValue();
            buffer.append(filterItem.getKey()).append(" ").append("=").append(" ");
            if (filterValue instanceof String && !isBoolean(filterValue) && !isNumeric(filterValue)) {
                System.out.println("String instance: " + filterValue);
                buffer.append("'").append(filterValue).append("'");
            } else {
                buffer.append(filterValue);
            }
            buffer.append(" ").append("AND").append(" ");
        }
        String filterString = buffer.substring(0, buffer.length() - 5);
        System.out.println("Constructed filter: " + filterString);
        return filterString;
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

    @GetMapping("/receive")
    public ResponseEntity<Map<String, String>> receiveMessage() {
        Map<String, String> response = new HashMap<>();

        String jmsResponse = jmsTemplate.receiveAndConvert("DEV.QUEUE.2").toString();

        response.put("response", jmsResponse);
        return ResponseEntity.ok(response);
    }

}

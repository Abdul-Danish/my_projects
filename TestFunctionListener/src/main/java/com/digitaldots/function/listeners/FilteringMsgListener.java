package com.digitaldots.function.listeners;

import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.adapter.FilteringMessageListenerAdapter;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

//@Component
public class FilteringMsgListener extends FilteringMessageListenerAdapter<String, Object> {

    public FilteringMsgListener(MessageListener<String, Object> delegate, RecordFilterStrategy<String, Object> recordFilterStrategy) {
        super(delegate, recordFilterStrategy);
    }

}

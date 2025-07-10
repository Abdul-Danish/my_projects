package com.digitaldots.function.listeners;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageDelegateListener implements MessageListener<String, Object> {

  @Override
  public void onMessage(ConsumerRecord<String, Object> data) {
      log.info("Executing listener");
      log.info("data: {}", data.value());
  }

}

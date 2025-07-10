package com.ibmmq.receiver.model;

import jakarta.jms.Message;

public interface MessageListener {

    void onMessage(Message message);
}

package com.ndpar;

import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;

public class JmsQueueListener implements MessageListener {

    @Autowired
    private TestResultAggregator testResultAggregator;

    public void onMessage(Message message) {
        testResultAggregator.decrementCounter();
    }
}
package com.ndpar;

import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class JmsQueueListener implements MessageListener {

    @Autowired
    private TestResultAggregator testResultAggregator;

    public void onMessage(Message message) {
//        System.out.println(message);
        if (message instanceof TextMessage) {
            try {
                ((TextMessage) message).getText();
                testResultAggregator.decrementCounter();
            } catch (JMSException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
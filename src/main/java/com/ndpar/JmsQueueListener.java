package com.ndpar;

import org.springframework.beans.factory.annotation.Required;

import javax.jms.Message;
import javax.jms.MessageListener;

public class JmsQueueListener implements MessageListener {

    private TestResultAggregator testResultAggregator;

    @Required
    public void setTestResultAggregator(TestResultAggregator testResultAggregator) {
        this.testResultAggregator = testResultAggregator;
    }

    public void onMessage(Message message) {
        testResultAggregator.decrementCounter();
    }
}
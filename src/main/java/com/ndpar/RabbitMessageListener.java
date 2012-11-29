package com.ndpar;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMessageListener implements MessageListener {

    @Autowired
    private TestResultAggregator testResultAggregator;

    @Override
    public void onMessage(Message message) {
        testResultAggregator.decrementCounter();
    }
}

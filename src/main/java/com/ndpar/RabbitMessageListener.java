package com.ndpar;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Profile(value = "rabbitmq")
public class RabbitMessageListener implements MessageListener {

    @Resource
    private TestResultAggregator rabbitTestAggregator;

    @Override
    public void onMessage(Message message) {
        rabbitTestAggregator.decrementCounter();
    }
}

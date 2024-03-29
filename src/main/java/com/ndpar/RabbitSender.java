package com.ndpar;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

@Service
@Profile(value = "rabbitmq")
@ManagedResource(objectName = "com.ndpar:name=RabbitSender")
public class RabbitSender implements MessageSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    private MessageProperties props = new MessageProperties();

    @Override
    @ManagedOperation(description = "Send text message")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "text", description = "message text")})
    public void send(final String text) {
        amqpTemplate.send("amq.fanout", "test", new Message(text.getBytes(), props));
    }
}

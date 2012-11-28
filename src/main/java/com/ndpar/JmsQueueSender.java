package com.ndpar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

@Service
@ManagedResource(objectName = "com.ndpar:name=JmsSender")
public class JmsQueueSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Resource
    private Queue testQueue;

    @ManagedOperation(description = "Send text message")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "text", description = "message text")})
    public void send(final String text) {
        this.jmsTemplate.send(this.testQueue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(text);
            }
        });
    }
}
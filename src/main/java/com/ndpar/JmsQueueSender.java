package com.ndpar;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

public class JmsQueueSender {

    private JmsTemplate jmsTemplate;
    private Queue testQueue;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setTestQueue(Queue testQueue) {
        this.testQueue = testQueue;
    }

    public void send(final String text) {
        this.jmsTemplate.send(this.testQueue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(text);
            }
        });
    }
}
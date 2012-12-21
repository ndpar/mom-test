package com.ndpar;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

public class TestResultAggregator implements BeanNameAware {

    private int messageSize = 10;
    private int loops = 10;

    private AtomicInteger counter = new AtomicInteger(0);
    private long start;

    private MessageSender messageSender;
    private String beanName;

    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Required
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @ManagedAttribute(description = "Message size in bytes")
    public int getMessageSize() {
        return messageSize;
    }

    @ManagedAttribute
    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    @ManagedAttribute
    public int getLoops() {
        return loops;
    }

    @ManagedAttribute
    public void setLoops(int loops) {
        this.loops = loops;
    }

    @ManagedOperation(description = "Run load test")
    public void runTest() {
        String msg = generateText(messageSize);
        counter.set(loops);
        start = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            taskExecutor.execute(new MessageSendTask(messageSender, msg));
        }
    }

    public void decrementCounter() {
        int c = counter.decrementAndGet();
        if (c == 0) {
            System.out.println(stats(System.currentTimeMillis() - start));
        }
    }

    private String stats(long duration) {
        StringBuffer result = new StringBuffer();
        result.append(beanName);
        result.append(" ---------------\n");
        result.append(String.format("Duration: %d (ms)\n", duration));
        result.append(String.format("Messages: %d\n", loops));
        result.append(String.format("Throughput: %d (msg/sec)\n", 1000 * loops / duration));
        result.append("--------------------------------\n");
        return result.toString();
    }

    private static String generateText(int bytes) {
        byte[] result = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            result[i] = 'a';
        }
        return new String(result);
    }


    private class MessageSendTask implements Runnable {

        private String text;
        private MessageSender jmsSender;

        public MessageSendTask(MessageSender jmsSender, String text) {
            this.jmsSender = jmsSender;
            this.text = text;
        }

        @Override
        public void run() {
            try {
                jmsSender.send(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

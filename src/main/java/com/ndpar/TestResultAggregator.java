package com.ndpar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ManagedResource(objectName = "com.ndpar:name=TestResultAggregator")
public class TestResultAggregator {

    private int messageSize = 10;
    private int loops = 10;

    private AtomicInteger counter = new AtomicInteger(0);
    private long start;

    @Resource
    private JmsQueueSender activeSender;

    @Resource
    private JmsQueueSender hornetSender;

    @Autowired
    private RabbitSender rabbitSender;

    @Autowired
    private TaskExecutor taskExecutor;

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

    @ManagedOperation(description = "Run ActiveMQ load test")
    public void runActiveTest() {
        String msg = generateText(messageSize);
        counter.set(loops);
        start = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            taskExecutor.execute(new JmsTask(activeSender, msg));
        }
    }

    @ManagedOperation(description = "Run HornetQ load test")
    public void runHornetTest() {
        String msg = generateText(messageSize);
        counter.set(loops);
        start = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            taskExecutor.execute(new JmsTask(hornetSender, msg));
        }
    }

    @ManagedOperation(description = "Run RabbitMQ load test")
    public void runRabbitTest() {
        String msg = generateText(messageSize);
        counter.set(loops);
        start = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            taskExecutor.execute(new RabbitTask(rabbitSender, msg));
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
        result.append("---------------\n");
        result.append(String.format("Duration: %d (ms)\n", duration));
        result.append(String.format("Messages: %d\n", loops));
        result.append(String.format("Throughput: %d (msg/sec)\n", 1000 * loops / duration));
        result.append("---------------\n");
        return result.toString();
    }

    private static String generateText(int bytes) {
        byte[] result = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            result[i] = 'a';
        }
        return new String(result);
    }


    private class JmsTask implements Runnable {

        private String text;
        private JmsQueueSender jmsSender;

        public JmsTask(JmsQueueSender jmsSender, String text) {
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

    private class RabbitTask implements Runnable {

        private String text;
        private RabbitSender rabbitSender;

        public RabbitTask(RabbitSender rabbitSender, String text) {
            this.rabbitSender = rabbitSender;
            this.text = text;
        }

        @Override
        public void run() {
            try {
                rabbitSender.send(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

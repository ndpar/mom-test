package com.ndpar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@ManagedResource(objectName = "com.ndpar:name=TestResultAggregator")
public class TestResultAggregator {

    private int loops = 10;

    private AtomicInteger counter = new AtomicInteger(0);
    private long start;

    @Autowired
    private JmsQueueSender sender;

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
        start = System.currentTimeMillis();
        counter.set(loops);
        for (int i = 0; i < loops; i++) {
            sender.send("Test");
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
}

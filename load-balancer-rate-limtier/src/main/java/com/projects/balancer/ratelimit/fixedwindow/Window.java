package com.projects.balancer.ratelimit.fixedwindow;

import java.util.concurrent.atomic.AtomicInteger;

public class Window {

    private final long startTime;

    private final AtomicInteger requestCount;

    public Window(long startTime) {
        this.startTime = startTime;
        this.requestCount = new AtomicInteger(0);
    }

    public int incremetn() {
        return requestCount.incrementAndGet();
    }

    public long getStartTime() {
        return startTime;
    }

    public int requestCount() {
        return requestCount.get();
    }
}
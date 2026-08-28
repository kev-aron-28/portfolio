package com.projects.balancer.rate_limit_strategies;

import java.util.concurrent.atomic.AtomicInteger;

public class Window {
    private final AtomicInteger count;
    private final long startTime;

    public Window(AtomicInteger count, long startTime) {
        this.count = count;
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public AtomicInteger getCount() {
        return count;
    }
}

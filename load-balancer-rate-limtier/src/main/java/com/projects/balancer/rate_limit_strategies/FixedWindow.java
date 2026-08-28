package com.projects.balancer.rate_limit_strategies;

import java.util.concurrent.ConcurrentHashMap;

public class FixedWindow implements RateLimitStrategy {
    private final int maxRequests;
    private final long windowMillis;
    private final ConcurrentHashMap<String, Window> clients = new ConcurrentHashMap<>();

    public FixedWindow(int maxRequests, long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
    }

    @Override
    public boolean allow(String clientId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
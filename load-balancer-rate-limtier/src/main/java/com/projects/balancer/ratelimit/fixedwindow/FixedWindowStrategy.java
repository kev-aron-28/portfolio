package com.projects.balancer.ratelimit.fixedwindow;

import java.util.concurrent.ConcurrentHashMap;

import com.projects.balancer.ratelimit.RateLimitStrategy;

public class FixedWindowStrategy implements RateLimitStrategy {
    private final int maxRequests;
    private final long windowMillis;
    private final ConcurrentHashMap<String, Window> clients = new ConcurrentHashMap<>();

    public FixedWindowStrategy(int maxRequests, long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
    }

    @Override
    public boolean allow(String clientId) {
        long now = System.currentTimeMillis();

        Window window = clients.compute(clientId, (key, currentWindow) -> {
            if(currentWindow == null || now - currentWindow.getStartTime() >= windowMillis) {
                return new Window(now);
            }

            return currentWindow;
        });
        
        int count = window.incremetn();

        return count <= maxRequests;
    }
    
}
package com.projects.balancer.ratelimit.slidingwindow;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

import com.projects.balancer.ratelimit.RateLimitStrategy;

public class SlidingWindowStrategy implements RateLimitStrategy {
    private final long windowMillis;
    private final int maxRequests;


    private final ConcurrentHashMap<String, Deque<Long>> clients = new ConcurrentHashMap<>();

    public SlidingWindowStrategy(long windowMillis, int maxRequests) {
        this.windowMillis = windowMillis;
        this.maxRequests = maxRequests;
    }

    @Override
    public boolean allow(String clientId) {
        long now = System.currentTimeMillis();

        Deque<Long> timestamps = clients.computeIfAbsent(clientId, key -> new ArrayDeque<>());

        synchronized (clients) {
            long startWindow = now - windowMillis;
            while(!timestamps.isEmpty() && timestamps.peekFirst() <= startWindow) {
                timestamps.pollFirst();
            }

            if(timestamps.size() >= maxRequests) {
                return false;
            }

            timestamps.add(now);
            
            return true;
        }
    }
}

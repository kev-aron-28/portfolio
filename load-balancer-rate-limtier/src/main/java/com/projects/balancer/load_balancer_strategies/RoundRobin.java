package com.projects.balancer.load_balancer_strategies;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.projects.balancer.BackendServer;
import com.projects.balancer.LoadBalancingStrategy;

public class RoundRobin implements LoadBalancingStrategy {
    private final AtomicInteger currentIndex = new AtomicInteger(0); 

    @Override
    public BackendServer select(List<BackendServer> servers) {
        if (servers.isEmpty()) {
            throw new IllegalStateException();
        }

        int index = currentIndex.getAndUpdate((c) -> (c + 1) % servers.size());

        return servers.get(index);
    }
    
}

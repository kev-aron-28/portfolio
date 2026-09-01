package com.projects.balancer.ratelimit;

public interface RateLimitStrategy {
    public boolean allow(String clientId);
}

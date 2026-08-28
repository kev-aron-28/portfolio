package com.projects.balancer.rate_limit_strategies;

public interface RateLimitStrategy {
    public boolean allow(String clientId);
}

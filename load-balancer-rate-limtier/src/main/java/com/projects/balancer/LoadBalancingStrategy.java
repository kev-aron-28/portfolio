package com.projects.balancer;

import java.util.List;

public interface LoadBalancingStrategy {
    public BackendServer select(List<BackendServer> servers);    
}

package com.projects;

import java.io.IOException;

import com.projects.balancer.LoadBalancer;
import com.projects.balancer.LoadBalancingStrategy;
import com.projects.balancer.load_balancer_strategies.RoundRobin;

public class App 
{
    public static void main( String[] args ) throws IOException
    {
        LoadBalancingStrategy strategy = new RoundRobin();
        LoadBalancer load = new LoadBalancer(3000,5, strategy);

        load.registerBackend("localhost", 8080);
        load.registerBackend("localhost", 8081);

        load.start();
    }
}

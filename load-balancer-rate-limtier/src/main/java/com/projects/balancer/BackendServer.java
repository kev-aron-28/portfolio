package com.projects.balancer;

public class BackendServer {
    private final String host;
    private final int port;

    public BackendServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}

package com.projects.node;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PhysicalNode {
    private final UUID id = UUID.randomUUID();
    private final String host;
    private final int port;
    private final NodeStatus status;
    private final Map<String, String> storage = new ConcurrentHashMap<>();

    public PhysicalNode(String host, int port, NodeStatus status) {
        this.host = host;
        this.port = port;
        this.status = status;
    }

    public void put(String key, String value) {
        storage.put(key,value);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(storage.get(key));
    }

    public void delete(String key) {
        storage.remove(key);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public UUID getId() {
        return id;
    }
}

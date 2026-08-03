package com.projects.node;

import java.util.Optional;
import java.util.UUID;

import com.projects.storage.StorageEngine;

public class PhysicalNode {
    private final UUID id = UUID.randomUUID();
    private final String host;
    private final int port;
    private final NodeStatus status;
    private final StorageEngine storage;

    public PhysicalNode(String host, int port, NodeStatus status) {
        this.host = host;
        this.port = port;
        this.status = status;
        this.storage = new StorageEngine();
    }

    public void put(String key, String value) {
        storage.put(key,value);
    }

    public Optional<String> get(String key) {
        return storage.get(key);
    }

    public void delete(String key) {
        storage.delete(key);
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

    public int size() {
        return storage.size();
    }

    public void showContent() {
        System.out.println(host);
        System.out.println("----------");
        storage.print();
    }
}

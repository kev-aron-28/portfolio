package com.projects.storage;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StorageEngine {
    private final Map<String, String> storage = new ConcurrentHashMap<>();

    public Optional<String> get(String key) {
        return Optional.ofNullable(storage.get(key));
    }

    public void put(String key, String value) {
        storage.put(key, value);
    }

    public void delete(String key) {
        storage.remove(key);
    }

    public boolean contains(String key) {
        return storage.keySet().contains(key);
    }

    public int size() {
        return storage.size();
    }

    public void print() {
        storage
        .entrySet()
        .forEach(e -> System.out.println(e.getKey() + "-" +e.getValue()));
    } 

    public Set<Map.Entry<String, String>> records() {
        return storage.entrySet();
    }
}

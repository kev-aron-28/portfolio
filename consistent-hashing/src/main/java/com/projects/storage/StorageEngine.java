package com.projects.storage;

import java.util.Map;
import java.util.Optional;
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
}

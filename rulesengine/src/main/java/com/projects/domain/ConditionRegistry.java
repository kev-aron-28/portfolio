package com.projects.domain;

import java.util.HashMap;

// This will recive a context containg everything necesary to execute the test on the rule
public class ConditionRegistry<T> {
    HashMap<String, Condition<T>> registry = new HashMap<>();

    public void add(String key, Condition<T> condition) {
        registry.put(key, condition);
    }

    public Condition<T> get(String key) {
        return registry.getOrDefault(key, null);
    }
     
}

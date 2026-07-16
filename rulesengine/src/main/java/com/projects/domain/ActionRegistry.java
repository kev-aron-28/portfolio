package com.projects.domain;
import java.util.HashMap;
import java.util.List;

public class ActionRegistry<C> {
    HashMap<String, ActionBuilder<C>> actions = new HashMap<>();


    public void add(String key, ActionBuilder<C> action) {
        actions.put(key, action);
    }

    public Action<C> get(String key, List<String> params) {
        if(actions.containsKey(key)) {
            return actions.get(key).build(params);
        }

        return null;
    }

}

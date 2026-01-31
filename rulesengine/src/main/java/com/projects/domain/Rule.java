package com.projects.domain;

import java.util.ArrayList;
import java.util.List;

public class Rule<C> {
    private final String id;
    private final List<Action<C>> actions = new ArrayList<>();
    private final List<Condition<C>> conditions = new ArrayList<>();

    public Rule(String id) {
        this.id = id;
    }

    // Helps to go thru each condition defined on the list
    // and if each conditions is true then you have the method execute
    public boolean matches(C context) {
        return conditions.stream().allMatch(c -> c.test(context));
    }

    public void execute(C context) {
        actions.stream().forEach(a -> a.execute(context));
    }

    public List<Action<C>> getActions() {
        return actions;
    }

    public String getId() {
        return id;
    }

    public List<Condition<C>> getConditions() {
        return conditions;
    }

    public void addAction(Action<C> action) {
        this.actions.add(action);
    }

    public void addCondition(Condition<C> condition) {
        this.conditions.add(condition);
    }

    public boolean hasActions() {
        return this.actions.isEmpty();
    }

    public boolean hasConditions() {
        return this.conditions.isEmpty();
    }
}

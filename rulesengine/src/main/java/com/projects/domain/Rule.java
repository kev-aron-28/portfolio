package com.projects.domain;

import java.util.ArrayList;
import java.util.List;

public class Rule<C> {
    private final String id;
    private final List<Action<C>> actions = new ArrayList<>();
    private Condition<C> root = null;

    public Rule(String id) {
        this.id = id;
    }

    // Helps to go thru each condition defined on the list
    // and if each conditions is true then you have the method execute
    public boolean matches(C context) {
        return root.test(context);
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

    public Condition<C> getRooCondition() {
        return root;
    }

    public void addAction(Action<C> action) {
        this.actions.add(action);
    }

    public void setRootCondition(Condition<C> condition) {
        this.root = condition;
    }

    public boolean hasActions() {
        return this.actions.isEmpty() != true;
    }

    public boolean hasConditions() {
        return root == null ? false : true;
    }
}

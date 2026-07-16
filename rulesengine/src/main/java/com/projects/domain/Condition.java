package com.projects.domain;

@FunctionalInterface
public interface Condition<T> {
    boolean test(T context);
}

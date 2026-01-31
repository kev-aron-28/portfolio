package com.projects.domain;

@FunctionalInterface
public interface Action<T> {
    void execute(T context);
}

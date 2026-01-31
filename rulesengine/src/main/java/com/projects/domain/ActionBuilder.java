package com.projects.domain;

import java.util.List;

@FunctionalInterface
public interface ActionBuilder<T> {
    Action<T> build(List<String> params);
}

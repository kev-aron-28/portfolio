package com.portfolio.backend.domain.Exceptions;

import java.util.List;

import com.portfolio.backend.domain.Role;
import com.portfolio.backend.domain.User;

public class FeatureContext {
    private final User userId;
    private final List<Role> role;

    public FeatureContext(User userId, List<Role> role) {
        this.userId = userId;
        this.role = role;
    }

    public List<Role> getRole() {
        return role;
    }

    public User getUser() {
        return userId;
    }
}

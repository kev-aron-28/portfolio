package com.portfolio.backend.domain.Exceptions;

import java.util.UUID;

import com.portfolio.backend.domain.Role;

public class FeatureContext {
    private final UUID userId;
    private final Role role;

    public FeatureContext(UUID userId, Role role) {
        this.userId = userId;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public UUID getUserId() {
        return userId;
    }
}

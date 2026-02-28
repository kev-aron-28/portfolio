package com.portfolio.backend.domain;

import java.util.UUID;

public class User {
    private UUID userId;

    public User(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}

package com.portfolio.backend.domain;

import java.util.UUID;

public class User {
    private final UUID userId;

    public User(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;

        if(obj == this) return true;

        if(!(obj instanceof User)) return false;

        User user = (User) obj;

        return this.userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

}

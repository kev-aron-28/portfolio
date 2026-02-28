package com.portfolio.backend.domain;

import java.util.Objects;
import java.util.UUID;

public class Role {
    private final UUID id;
    private String name;

    public Role(UUID id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(!(obj instanceof Role)) return false;

        Role role = (Role) obj;

        return name.equals(role.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

package com.portfolio.backend.domain;

import java.util.UUID;

import com.portfolio.backend.domain.Exceptions.InvalidId;
import com.portfolio.backend.domain.Exceptions.InvalidRoleName;

public class Role {
    private final UUID id;
    private String name;

    public Role(UUID id, String name) {
        this.id = evaluateId(id);
        this.name = evaluateName(name);
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

    private String evaluateName(String name) {
        if(name == null || name.isBlank()) throw new InvalidRoleName();

        return name;
    }

    private UUID evaluateId(UUID id) {
        if(id == null) {
            throw new InvalidId();
        }

        return id;
    }
}

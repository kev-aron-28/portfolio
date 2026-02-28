package com.portfolio.backend.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.portfolio.backend.domain.Exceptions.FeatureContext;
import com.portfolio.backend.domain.Exceptions.InvalidFeatureKey;
import com.portfolio.backend.domain.Exceptions.InvalidId;
import com.portfolio.backend.domain.Exceptions.InvalidRollout;

public class FeatureFlag {
    private final UUID id;
    private final String key;
    private final boolean enabled;
    private String description;
    private Integer rolloutPercentage;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<Role> allowedRoles;
    private final List<User> allowedUsers;

    public FeatureFlag(UUID id, String key, boolean enabled, Integer rolloutPercentage, LocalDateTime createdAt, LocalDateTime updatedAt, List<Role> allowedRoles, List<User> allowedUsers) {
        this.id = evaluateId(id);
        this.key = evaluateKey(key);
        this.enabled = enabled;
        this.rolloutPercentage = evaluateRollout(rolloutPercentage);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.allowedRoles = new ArrayList<>(allowedRoles);
        this.allowedUsers = new ArrayList<>(allowedUsers);
    }


    public boolean evaluate(FeatureContext context) {
        if(!this.enabled) return false;

        if(this.noRulesDefined()) return true;


        return false;
    }

    public UUID getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Integer getRolloutPercentage() {
        return rolloutPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean noRulesDefined() {
        return this.rolloutPercentage == null && this.allowedRoles.isEmpty() && this.allowedRoles.isEmpty();
    }

    private UUID evaluateId(UUID id) {
        if(id == null) {
            throw new InvalidId();
        }

        return id;
    }

    private String evaluateKey(String key) {
        if(key == null || key.isBlank()) {
            throw new InvalidFeatureKey();
        } 

        return key;
    }

    private Integer evaluateRollout(Integer rollout)  {
        if(rollout == null) return null;

        if(rollout < 0 || rollout > 100) {
            throw new InvalidRollout();
        }

        return rollout;
    }
} 

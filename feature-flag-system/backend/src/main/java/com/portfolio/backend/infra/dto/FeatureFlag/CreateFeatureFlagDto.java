package com.portfolio.backend.infra.dto.FeatureFlag;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateFeatureFlagDto {
    @NotBlank
    private final String key;
    
    @NotBlank
    private final String description;

    @PositiveOrZero
    private final Integer rolloutPercentage;

    private final List<String> roles;

    private final List<String> users;

    public CreateFeatureFlagDto(String key, String description, Integer rolloutPercentage, List<String> roles, List<String> users) {
        this.key = key;
        this.description = description;
        this.rolloutPercentage = rolloutPercentage;
        this.roles = roles;
        this.users = users;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public Integer getRolloutPercentage() {
        return rolloutPercentage;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getUsers() {
        return users;
    }

    

}

package com.portfolio.backend.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.backend.domain.FeatureFlag;
import com.portfolio.backend.domain.FeatureFlagRepository;
import com.portfolio.backend.domain.Role;
import com.portfolio.backend.domain.User;
import com.portfolio.backend.infra.dto.FeatureFlag.CreateFeatureFlagDto;

@Service
public class SaveFeatureFlagUseCase {
    private final FeatureFlagRepository repository;

    public SaveFeatureFlagUseCase(FeatureFlagRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void run(CreateFeatureFlagDto featureFlagBody) {
        List<User> users = featureFlagBody.getUsers().stream()
            .map(id -> new User(UUID.fromString(id)))
            .toList();

        List<Role> roles = featureFlagBody.getRoles().stream()
            .map(r -> new Role(UUID.randomUUID(), r))
            .toList();

        FeatureFlag flag = new FeatureFlag(
            UUID.randomUUID(),
            featureFlagBody.getKey(),
            true,
            featureFlagBody.getDescription(),
            featureFlagBody.getRolloutPercentage(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            roles,
            users
        );

        this.repository.save(flag);
    }
}

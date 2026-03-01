package com.portfolio.backend.domain;

import java.util.UUID;

public interface FeatureFlagRepository {
    void save(FeatureFlag featureFlag);
    boolean exists(UUID id);
}

package com.portfolio.backend.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.backend.domain.FeatureFlag;
import com.portfolio.backend.domain.FeatureFlagRepository;

@Service
public class SaveFeatureFlagUseCase {
    private final FeatureFlagRepository repository;

    public SaveFeatureFlagUseCase(FeatureFlagRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void run(FeatureFlag featureFlag) {
        repository.save(featureFlag);
    }
}

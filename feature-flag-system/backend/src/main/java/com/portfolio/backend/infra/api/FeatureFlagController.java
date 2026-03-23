package com.portfolio.backend.infra.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.backend.application.SaveFeatureFlagUseCase;
import com.portfolio.backend.infra.dto.FeatureFlag.CreateFeatureFlagDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/features")
public class FeatureFlagController {
    private final SaveFeatureFlagUseCase saveFeatureFlagUseCase;

    public FeatureFlagController(SaveFeatureFlagUseCase saveFeatureFlagUseCase) {
        this.saveFeatureFlagUseCase = saveFeatureFlagUseCase;
    }

    @PostMapping()
    public ResponseEntity<Void> saveFeatureFlag(@RequestBody @Valid CreateFeatureFlagDto featureFlagBody) {
        this.saveFeatureFlagUseCase.run(featureFlagBody);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

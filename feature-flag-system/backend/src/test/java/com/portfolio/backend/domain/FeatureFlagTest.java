package com.portfolio.backend.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.portfolio.backend.domain.Exceptions.InvalidFeatureKey;
import com.portfolio.backend.domain.Exceptions.InvalidId;
import com.portfolio.backend.domain.Exceptions.InvalidRollout;

public class FeatureFlagTest {
    
    @Test
    void shouldThrowWhenFeatureFlagIdIsNull() {
        assertThrows(
            InvalidId.class,
            () -> new FeatureFlag(null, "KEY", false, "", 10, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>())
        );
    }

    @Test
    void shouldThrowWhenKeyIsInvalid() {
        assertThrows(
            InvalidFeatureKey.class,
            () -> new FeatureFlag(UUID.randomUUID(), null, false, "", 10, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>())
        );
    }

    @Test
    void shouldThrowWhenRolloutIsInvalid() {
        assertThrows(
            InvalidRollout.class,
            () -> new FeatureFlag(UUID.randomUUID(), "KEY_FLAG", false, "", -1, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>())
        );
    }
}

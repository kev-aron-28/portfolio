package com.portfolio.backend.domain;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.portfolio.backend.domain.Exceptions.InvalidId;
import com.portfolio.backend.domain.Exceptions.InvalidRoleName;

public class RoleTest {
    
    @Test
    public void shouldFailWhenInvalidName() {
        assertThrows(InvalidRoleName.class,() -> new Role(UUID.randomUUID(),null));
    }

    @Test
    public void shouldFailWhenInvalidId() {
        assertThrows(InvalidId.class,() -> new Role(null,"Name"));
    }
}

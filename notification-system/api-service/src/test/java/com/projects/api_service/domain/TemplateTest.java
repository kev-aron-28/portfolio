package com.projects.api_service.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.projects.api_service.domain.errors.InvalidArgument;

public class TemplateTest {
    
    @Test
    public void shouldTestInvariants() {
        assertThrows(InvalidArgument.class, () -> {
            new Template(null, null, null, null);
        });
    }
}

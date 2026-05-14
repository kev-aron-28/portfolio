package com.projects.api_service.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.projects.api_service.domain.errors.InvalidArgument;
import com.projects.api_service.domain.errors.InvalidTemplate;

public class TemplateTest {
    
    @Test
    public void shouldTestInvariants() {
        assertThrows(InvalidArgument.class, () -> {
            new Template(null, null, null, null);
        });
    }

    @Test
    public void shouldThrowWhenInvalidTemplate() {
        String invalidTemplate = "This is an invalid template {}";

        assertThrows(InvalidTemplate.class, () -> {
            new Template(
                "Test template",
                NotificationChannel.EMAIL,
                "Test subject",
                invalidTemplate
            );
        });
    }
}

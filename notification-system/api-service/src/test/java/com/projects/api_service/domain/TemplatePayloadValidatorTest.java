package com.projects.api_service.domain;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projects.api_service.domain.errors.MissingTemplateVariable;
import com.projects.api_service.domain.services.TemplatePayloadValidator;

public class TemplatePayloadValidatorTest {
    private TemplatePayloadValidator validator;

    @BeforeEach
    public void beforeEach() {
        this.validator = new TemplatePayloadValidator();
    }

    @Test
    public void shouldThrowIfNotCompleteFields() {
        String template = "Hi {{ name }}";

        Map<String, Object> payload = new HashMap<>();
        payload.put("Other", "Kevin");

        assertThrows(MissingTemplateVariable.class, () -> {
            validator.validate(template, payload);
        });
    }
}

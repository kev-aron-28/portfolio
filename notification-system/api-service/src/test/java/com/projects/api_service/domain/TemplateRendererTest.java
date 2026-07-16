package com.projects.api_service.domain;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projects.api_service.domain.services.TemplateRenderer;

public class TemplateRendererTest {
    private TemplateRenderer renderer;

    @BeforeEach
    public void beforeEach() {
        this.renderer = new TemplateRenderer();
    }

    @Test
    public void shouldRenderAllVariablesInTemplate() {
        String template = "Hello {{name}}";
        Map<String,Object> payload = new HashMap<>();
        payload.put("name", "kevin");

        String expected = "Hello kevin";

        String result = this.renderer.render(template, payload);
    
        assertEquals(expected, result);
    }
}

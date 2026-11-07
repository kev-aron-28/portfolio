package com.example.aiinventoryassistant.assistant.application;

import java.util.Map;

public record ToolDefinition(String type, String name, String description,
                             Map<String, Object> parameters) {
    public ToolDefinition {
        if (!"function".equals(type) || name == null || name.isBlank()
                || description == null || description.isBlank() || parameters == null) {
            throw new IllegalArgumentException("A valid function tool definition is required");
        }
    }
}

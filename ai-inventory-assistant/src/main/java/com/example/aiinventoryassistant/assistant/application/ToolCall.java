package com.example.aiinventoryassistant.assistant.application;

import java.util.Map;

public record ToolCall(String callId, String name, Map<String, Object> arguments) {
    public ToolCall {
        if (callId == null || callId.isBlank() || name == null || name.isBlank() || arguments == null) {
            throw new IllegalArgumentException("A tool call requires an id, name and arguments");
        }
    }
}

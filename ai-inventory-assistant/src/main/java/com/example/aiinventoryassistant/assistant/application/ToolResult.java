package com.example.aiinventoryassistant.assistant.application;

public record ToolResult(String callId, String output) {
    public ToolResult {
        if (callId == null || callId.isBlank() || output == null) {
            throw new IllegalArgumentException("A tool result requires a call id and output");
        }
    }
}

package com.example.aiinventoryassistant.assistant.api;

import jakarta.validation.constraints.NotBlank;

public record AssistantMessageRequest(@NotBlank String message) {
}

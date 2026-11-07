package com.example.aiinventoryassistant.assistant.application;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

final class FakeOpenAiClient implements OpenAiClient {
    private final Queue<ModelResponse> responses = new ArrayDeque<>();

    FakeOpenAiClient thenRespond(ModelResponse response) {
        responses.add(response);
        return this;
    }

    @Override
    public ModelResponse respond(String userMessage, List<ToolDefinition> tools) {
        if (responses.isEmpty()) {
            throw new IllegalStateException("No fake response configured");
        }
        return responses.remove();
    }
}

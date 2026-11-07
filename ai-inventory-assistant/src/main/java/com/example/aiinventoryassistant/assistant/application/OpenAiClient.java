package com.example.aiinventoryassistant.assistant.application;

import java.util.List;

public interface OpenAiClient {
    ModelResponse respond(String userMessage, List<ToolDefinition> tools);

    default ModelResponse respond(String userMessage, List<ToolDefinition> tools,
                                  List<ToolResult> toolResults) {
        return respond(userMessage, tools);
    }

    default ModelResponse respond(String userMessage, List<ToolDefinition> tools,
                                  List<ToolResult> toolResults, String previousResponseId) {
        return respond(userMessage, tools, toolResults);
    }

    record ModelResponse(String text, ToolCall toolCall, String responseId) {
        public static ModelResponse text(String text) {
            return new ModelResponse(text, null, null);
        }

        public static ModelResponse toolCall(ToolCall toolCall) {
            return new ModelResponse(null, toolCall, null);
        }

        public ModelResponse withResponseId(String id) { return new ModelResponse(text, toolCall, id); }

        public boolean hasToolCall() {
            return toolCall != null;
        }
    }
}

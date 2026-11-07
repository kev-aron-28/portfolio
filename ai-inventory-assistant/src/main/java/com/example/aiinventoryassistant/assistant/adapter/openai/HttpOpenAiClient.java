package com.example.aiinventoryassistant.assistant.adapter.openai;

import com.example.aiinventoryassistant.assistant.application.OpenAiClient;
import com.example.aiinventoryassistant.assistant.application.ToolCall;
import com.example.aiinventoryassistant.assistant.application.ToolDefinition;
import com.example.aiinventoryassistant.assistant.application.ToolResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HttpOpenAiClient implements OpenAiClient {
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newHttpClient();
    private final String apiKey;
    private final String model;

    public HttpOpenAiClient(ObjectMapper mapper, @Value("${app.openai.api-key:}") String apiKey,
                            @Value("${app.openai.model:gpt-5.4-mini}") String model) {
        this.mapper = mapper; this.apiKey = apiKey; this.model = model;
    }

    @Override
    public ModelResponse respond(String message, List<ToolDefinition> tools) {
        return request(Map.of("model", model, "input", message, "tools", apiTools(tools)));
    }

    @Override
    public ModelResponse respond(String message, List<ToolDefinition> tools,
                                 List<ToolResult> results, String previousResponseId) {
        List<Map<String, Object>> input = results.stream()
                .map(result -> Map.<String, Object>of("type", "function_call_output",
                        "call_id", result.callId(), "output", result.output())).toList();
        return request(Map.of("model", model, "previous_response_id", previousResponseId,
                "input", input, "tools", apiTools(tools)));
    }

    private ModelResponse request(Map<String, Object> payload) {
        if (apiKey.isBlank()) throw new IllegalStateException("OPENAI_API_KEY is not configured");
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/responses"))
                    .header("Authorization", "Bearer " + apiKey).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload))).build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) throw new IllegalStateException("OpenAI API error: " + response.body());
            return parse(mapper.readTree(response.body()));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not call OpenAI Responses API", exception);
        }
    }

    private ModelResponse parse(JsonNode response) {
        String responseId = response.path("id").asText(null);
        for (JsonNode item : response.path("output")) {
            if ("function_call".equals(item.path("type").asText())) {
                Map<String, Object> arguments;
                try { arguments = mapper.readValue(item.path("arguments").asText("{}"), Map.class); }
                catch (Exception e) { throw new IllegalStateException("Invalid tool arguments from OpenAI", e); }
                return ModelResponse.toolCall(new ToolCall(item.path("call_id").asText(), item.path("name").asText(), arguments)).withResponseId(responseId);
            }
        }
        return ModelResponse.text(response.path("output_text").asText("")).withResponseId(responseId);
    }

    private List<Map<String, Object>> apiTools(List<ToolDefinition> tools) {
        return tools.stream().map(tool -> Map.of("type", "function", "name", tool.name(),
                "description", tool.description(), "parameters", tool.parameters(), "strict", true)).toList();
    }
}

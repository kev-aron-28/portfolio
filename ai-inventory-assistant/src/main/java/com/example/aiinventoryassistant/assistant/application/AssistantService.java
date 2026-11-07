package com.example.aiinventoryassistant.assistant.application;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssistantService {
    private static final int MAX_TOOL_ROUNDS = 10;
    private final OpenAiClient openAiClient;
    private final InventoryToolDispatcher dispatcher;
    private final boolean writeToolsEnabled;

    public AssistantService(OpenAiClient openAiClient, InventoryToolDispatcher dispatcher) {
        this(openAiClient, dispatcher, true);
    }

    @Autowired
    public AssistantService(OpenAiClient openAiClient, InventoryToolDispatcher dispatcher,
                            @Value("${app.assistant.write-tools-enabled:false}") boolean writeToolsEnabled) {
        this.openAiClient = openAiClient;
        this.dispatcher = dispatcher;
        this.writeToolsEnabled = writeToolsEnabled;
    }

    public String respond(String userMessage) {
        List<ToolDefinition> tools = InventoryToolCatalog.tools(writeToolsEnabled);
        List<ToolResult> toolResults = new ArrayList<>();
        OpenAiClient.ModelResponse response = openAiClient.respond(userMessage, tools);
        for (int round = 0; response.hasToolCall(); round++) {
            if (round >= MAX_TOOL_ROUNDS) throw new IllegalStateException("Assistant exceeded the maximum number of tool rounds");
            toolResults.add(dispatcher.dispatch(response.toolCall()));
            response = openAiClient.respond(userMessage, tools, List.copyOf(toolResults), response.responseId());
        }
        if (response.text() == null || response.text().isBlank()) throw new IllegalStateException("OpenAI returned neither a final message nor a tool call");
        return response.text();
    }
}

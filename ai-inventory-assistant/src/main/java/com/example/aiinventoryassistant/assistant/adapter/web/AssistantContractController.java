package com.example.aiinventoryassistant.assistant.adapter.web;

import com.example.aiinventoryassistant.assistant.api.AssistantMessageRequest;
import com.example.aiinventoryassistant.assistant.api.AssistantMessageResponse;
import com.example.aiinventoryassistant.assistant.application.InventoryToolCatalog;
import com.example.aiinventoryassistant.assistant.application.AssistantService;
import com.example.aiinventoryassistant.assistant.application.ToolDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@RestController
@RequestMapping("/api/assistant")
public class AssistantContractController {
    private final AssistantService assistant;
    private final boolean writeToolsEnabled;

    public AssistantContractController(AssistantService assistant,
                                       @Value("${app.assistant.write-tools-enabled:false}") boolean writeToolsEnabled) {
        this.assistant = assistant;
        this.writeToolsEnabled = writeToolsEnabled;
    }
    @GetMapping("/tools")
    public List<ToolDefinition> tools() {
        return InventoryToolCatalog.tools(writeToolsEnabled);
    }

    @PostMapping("/messages")
    public AssistantMessageResponse messages(@RequestBody AssistantMessageRequest request) {
        return new AssistantMessageResponse(assistant.respond(request.message()));
    }
}

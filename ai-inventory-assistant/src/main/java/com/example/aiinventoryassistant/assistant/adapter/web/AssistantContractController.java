package com.example.aiinventoryassistant.assistant.adapter.web;

import com.example.aiinventoryassistant.assistant.api.AssistantMessageRequest;
import com.example.aiinventoryassistant.assistant.api.AssistantMessageResponse;
import com.example.aiinventoryassistant.assistant.application.InventoryToolCatalog;
import com.example.aiinventoryassistant.assistant.application.ToolDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assistant")
public class AssistantContractController {
    @GetMapping("/tools")
    public List<ToolDefinition> tools() {
        return InventoryToolCatalog.tools();
    }

    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public AssistantMessageResponse messages(@RequestBody AssistantMessageRequest request) {
        return new AssistantMessageResponse("Assistant integration is not implemented yet: " + request.message());
    }
}

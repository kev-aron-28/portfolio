package com.example.aiinventoryassistant.assistant.application;

import com.example.aiinventoryassistant.inventory.application.InventoryService;
import com.example.aiinventoryassistant.inventory.domain.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AssistantServiceTest {
    @Test
    void repeatsTheLoopForMultipleToolCalls() {
        InventoryService inventory = mock(InventoryService.class);
        when(inventory.search("laptop")).thenReturn(List.of(new Product("LAP-001", "Laptop")));
        when(inventory.search("monitor")).thenReturn(List.of(new Product("MON-001", "Monitor")));
        InventoryToolDispatcher dispatcher = new InventoryToolDispatcher(inventory, new ObjectMapper());
        OpenAiClient client = new OpenAiClient() {
            private int calls;
            public ModelResponse respond(String message, List<ToolDefinition> tools) {
                return ModelResponse.toolCall(new ToolCall("one", "search_products", Map.of("query", "laptop")));
            }
            public ModelResponse respond(String message, List<ToolDefinition> tools, List<ToolResult> results) {
                calls++;
                return calls == 1
                        ? ModelResponse.toolCall(new ToolCall("two", "search_products", Map.of("query", "monitor")))
                        : ModelResponse.text("Found both products");
            }
        };

        assertEquals("Found both products", new AssistantService(client, dispatcher).respond("Find laptops and monitors"));
        verify(inventory).search("laptop");
        verify(inventory).search("monitor");
    }

    @Test
    void usesMockedOpenAiClientForACompleteProposalFlow() {
        InventoryService inventory = mock(InventoryService.class);
        var movement = new com.example.aiinventoryassistant.inventory.domain.Movement(
                java.util.UUID.randomUUID(), "LAP-001", "NORTH", 2,
                com.example.aiinventoryassistant.inventory.domain.Movement.Type.EXIT,
                com.example.aiinventoryassistant.inventory.domain.Movement.Status.PENDING);
        when(inventory.propose("LAP-001", "NORTH", 2, movement.type())).thenReturn(movement);
        InventoryToolDispatcher dispatcher = new InventoryToolDispatcher(inventory, new ObjectMapper());
        OpenAiClient client = mock(OpenAiClient.class);
        when(client.respond(anyString(), anyList())).thenReturn(OpenAiClient.ModelResponse.toolCall(
                new ToolCall("call-1", "propose_stock_movement", Map.of(
                        "sku", "LAP-001", "warehouse", "NORTH", "quantity", 2, "type", "EXIT"))));
        when(client.respond(anyString(), anyList(), anyList(), any())).thenReturn(
                OpenAiClient.ModelResponse.text("I prepared a pending exit for 2 laptops. Please confirm it."));

        String response = new AssistantService(client, dispatcher).respond("Remove two laptops from North");

        assertEquals("I prepared a pending exit for 2 laptops. Please confirm it.", response);
        verify(inventory).propose("LAP-001", "NORTH", 2, movement.type());
        verify(inventory, never()).confirm(any());
    }
}

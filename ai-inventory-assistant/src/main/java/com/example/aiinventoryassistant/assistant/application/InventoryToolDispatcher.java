package com.example.aiinventoryassistant.assistant.application;

import com.example.aiinventoryassistant.inventory.application.InventoryService;
import com.example.aiinventoryassistant.inventory.domain.Movement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class InventoryToolDispatcher {
    private final InventoryService inventory;
    private final ObjectMapper objectMapper;
    private final Set<String> supportedTools;

    public InventoryToolDispatcher(InventoryService inventory, ObjectMapper objectMapper) {
        this.inventory = inventory;
        this.objectMapper = objectMapper;
        this.supportedTools = InventoryToolCatalog.tools().stream()
                .map(ToolDefinition::name)
                .collect(Collectors.toUnmodifiableSet());
    }

    public ToolResult dispatch(ToolCall call) {
        if (!supportedTools.contains(call.name())) {
            throw new IllegalArgumentException("Unsupported tool: " + call.name());
        }
        Object result = switch (call.name()) {
            case "search_products" -> inventory.search(requiredText(call, "query"));
            case "get_product_stock" -> inventory.productStock(requiredText(call, "sku"));
            case "get_warehouse_stock" -> inventory.warehouseStock(requiredText(call, "warehouse"));
            case "propose_stock_movement" -> inventory.propose(
                    requiredText(call, "sku"),
                    requiredText(call, "warehouse"),
                    requiredPositiveQuantity(call),
                    movementType(call));
            default -> throw new IllegalStateException("Tool catalog and dispatcher are out of sync");
        };
        return new ToolResult(call.callId(), json(result));
    }

    private String requiredText(ToolCall call, String name) {
        Object value = call.arguments().get(name);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("Tool " + call.name() + " requires a non-blank '" + name + "'");
        }
        return text;
    }

    private long requiredPositiveQuantity(ToolCall call) {
        Object value = call.arguments().get("quantity");
        if (!(value instanceof Number number) || number.longValue() <= 0 || number.doubleValue() != number.longValue()) {
            throw new IllegalArgumentException("Tool propose_stock_movement requires a positive integer 'quantity'");
        }
        return number.longValue();
    }

    private Movement.Type movementType(ToolCall call) {
        String value = requiredText(call, "type");
        try {
            return Movement.Type.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Movement type must be ENTRY or EXIT", exception);
        }
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize tool result", exception);
        }
    }
}

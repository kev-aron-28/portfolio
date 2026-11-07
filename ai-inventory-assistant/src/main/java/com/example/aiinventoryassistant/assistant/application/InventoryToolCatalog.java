package com.example.aiinventoryassistant.assistant.application;

import java.util.List;
import java.util.Map;

public final class InventoryToolCatalog {
    private InventoryToolCatalog() {
    }

    public static List<ToolDefinition> tools() {
        return tools(true);
    }

    public static List<ToolDefinition> tools(boolean includeWriteTools) {
        List<ToolDefinition> tools = new java.util.ArrayList<>(List.of(
                function("search_products", "Search products by SKU or name.",
                        Map.of("type", "object", "properties", Map.of(
                                "query", Map.of("type", "string", "description", "SKU or product name to search for")),
                                "required", List.of("query"), "additionalProperties", false)),
                function("get_product_stock", "Get total and per-warehouse stock for a product.",
                        Map.of("type", "object", "properties", Map.of(
                                "sku", Map.of("type", "string", "description", "Product SKU")),
                                "required", List.of("sku"), "additionalProperties", false)),
                function("get_warehouse_stock", "Get all product stock in a warehouse.",
                        Map.of("type", "object", "properties", Map.of(
                                "warehouse", Map.of("type", "string", "description", "Warehouse code")),
                                "required", List.of("warehouse"), "additionalProperties", false)),
                function("propose_stock_movement", "Create a pending stock entry or exit. This never changes stock and always requires explicit confirmation through the backend.",
                        Map.of("type", "object", "properties", Map.of(
                                "sku", Map.of("type", "string", "description", "Product SKU"),
                                "warehouse", Map.of("type", "string", "description", "Warehouse code"),
                                "quantity", Map.of("type", "integer", "minimum", 1, "description", "Positive number of units"),
                                "type", Map.of("type", "string", "enum", List.of("ENTRY", "EXIT"))),
                                "required", List.of("sku", "warehouse", "quantity", "type"), "additionalProperties", false))
        ));
        if (!includeWriteTools) tools.removeIf(tool -> tool.name().equals("propose_stock_movement"));
        return List.copyOf(tools);
    }

    private static ToolDefinition function(String name, String description, Map<String, Object> parameters) {
        return new ToolDefinition("function", name, description, parameters);
    }
}

package com.example.aiinventoryassistant.assistant.application;

import com.example.aiinventoryassistant.inventory.application.InventoryService;
import com.example.aiinventoryassistant.inventory.domain.Product;
import com.example.aiinventoryassistant.inventory.domain.Movement;
import com.example.aiinventoryassistant.inventory.domain.Stock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InventoryToolDispatcherTest {
    private final InventoryService inventory = mock(InventoryService.class);
    private final InventoryToolDispatcher dispatcher = new InventoryToolDispatcher(inventory, new ObjectMapper());

    @Test
    void exposesProposalButNeverDirectConfirmationTool() {
        var names = InventoryToolCatalog.tools().stream().map(ToolDefinition::name).toList();
        assertTrue(names.contains("propose_stock_movement"));
        assertFalse(names.contains("confirm_stock_movement"));
    }

    @Test
    void dispatchesProductSearchToApplicationService() {
        when(inventory.search("laptop")).thenReturn(List.of(new Product("LAP-001", "Laptop")));

        ToolResult result = dispatcher.dispatch(new ToolCall("call-1", "search_products", Map.of("query", "laptop")));

        assertEquals("call-1", result.callId());
        assertTrue(result.output().contains("LAP-001"));
        verify(inventory).search("laptop");
    }

    @Test
    void rejectsUnknownTool() {
        assertThrows(IllegalArgumentException.class,
                () -> dispatcher.dispatch(new ToolCall("call-1", "delete_database", Map.of())));
        verifyNoInteractions(inventory);
    }

    @Test
    void rejectsInvalidMovementQuantity() {
        assertThrows(IllegalArgumentException.class, () -> dispatcher.dispatch(new ToolCall(
                "call-1", "propose_stock_movement",
                Map.of("sku", "LAP-001", "warehouse", "NORTH", "quantity", 0, "type", "EXIT"))));
        verifyNoInteractions(inventory);
    }

    @Test
    void rejectsMissingArguments() {
        assertThrows(IllegalArgumentException.class, () -> dispatcher.dispatch(
                new ToolCall("call-1", "get_product_stock", Map.of())));
        verifyNoInteractions(inventory);
    }

    @Test
    void rejectsInvalidMovementType() {
        assertThrows(IllegalArgumentException.class, () -> dispatcher.dispatch(new ToolCall(
                "call-1", "propose_stock_movement",
                Map.of("sku", "LAP-001", "warehouse", "NORTH", "quantity", 1, "type", "TRANSFER"))));
        verifyNoInteractions(inventory);
    }

    @Test
    void dispatchesProductStockLookup() {
        when(inventory.productStock("LAP-001")).thenReturn(List.of(new Stock("LAP-001", "NORTH", 12)));
        dispatcher.dispatch(new ToolCall("call-1", "get_product_stock", Map.of("sku", "LAP-001")));
        verify(inventory).productStock("LAP-001");
    }

    @Test
    void dispatchesWarehouseStockLookup() {
        when(inventory.warehouseStock("NORTH")).thenReturn(List.of(new Stock("LAP-001", "NORTH", 12)));
        dispatcher.dispatch(new ToolCall("call-1", "get_warehouse_stock", Map.of("warehouse", "NORTH")));
        verify(inventory).warehouseStock("NORTH");
    }

    @Test
    void dispatchesMovementProposalWithValidatedArguments() {
        Movement movement = new Movement(java.util.UUID.randomUUID(), "LAP-001", "NORTH", 2,
                Movement.Type.EXIT, Movement.Status.PENDING);
        when(inventory.propose("LAP-001", "NORTH", 2, Movement.Type.EXIT)).thenReturn(movement);

        ToolResult result = dispatcher.dispatch(new ToolCall("call-2", "propose_stock_movement", Map.of(
                "sku", "LAP-001", "warehouse", "NORTH", "quantity", 2, "type", "EXIT")));

        assertEquals("call-2", result.callId());
        assertTrue(result.output().contains("LAP-001"));
        verify(inventory).propose("LAP-001", "NORTH", 2, Movement.Type.EXIT);
    }
}

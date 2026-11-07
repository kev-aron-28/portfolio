package com.example.aiinventoryassistant.inventory.application;

import com.example.aiinventoryassistant.inventory.domain.*;
import java.util.List;
import java.util.UUID;

public class InventoryService {
    private final InventoryPort inventory;
    public InventoryService(InventoryPort inventory) { this.inventory = inventory; }
    public List<Product> search(String query) { return inventory.search(query); }
    public List<Stock> productStock(String sku) { return inventory.productStock(sku); }
    public List<Stock> warehouseStock(String warehouse) { return inventory.warehouseStock(warehouse); }
    public Movement propose(String sku, String warehouse, long quantity, Movement.Type type) {
        return inventory.propose(new Movement(UUID.randomUUID(), sku, warehouse, quantity, type, Movement.Status.PENDING));
    }
    public Movement confirm(UUID id) { return inventory.confirm(id); }
    public Movement findMovement(UUID id) { return inventory.findMovement(id); }
    public List<Movement> movements() { return inventory.movements(); }
}

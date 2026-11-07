package com.example.aiinventoryassistant.inventory.application;

import com.example.aiinventoryassistant.inventory.domain.*;
import java.util.List;
import java.util.UUID;

public interface InventoryPort {
    List<Product> search(String query);
    List<Stock> productStock(String sku);
    List<Stock> warehouseStock(String warehouse);
    Movement propose(Movement movement);
    Movement confirm(UUID id);
    Movement findMovement(UUID id);
    List<Movement> movements();
}

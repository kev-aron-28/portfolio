package com.example.aiinventoryassistant.inventory;
import com.example.aiinventoryassistant.inventory.application.*;
import org.springframework.context.annotation.*;

@Configuration
public class InventoryConfiguration {
    @Bean InventoryService inventoryService(InventoryPort port) { return new InventoryService(port); }
}

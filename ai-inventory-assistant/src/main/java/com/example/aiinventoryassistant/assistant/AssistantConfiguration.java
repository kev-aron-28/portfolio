package com.example.aiinventoryassistant.assistant;

import com.example.aiinventoryassistant.assistant.application.InventoryToolDispatcher;
import com.example.aiinventoryassistant.inventory.application.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class AssistantConfiguration {
    @Bean
    InventoryToolDispatcher inventoryToolDispatcher(InventoryService inventoryService, ObjectMapper objectMapper) {
        return new InventoryToolDispatcher(inventoryService, objectMapper);
    }
}

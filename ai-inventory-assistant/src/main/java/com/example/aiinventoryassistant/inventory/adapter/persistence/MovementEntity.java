package com.example.aiinventoryassistant.inventory.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import com.example.aiinventoryassistant.inventory.domain.Movement;

@Entity
@Table(name = "stock_movement")
public class MovementEntity {
    @Id UUID id;
    String sku;
    String warehouse;
    long quantity;
    @Enumerated(EnumType.STRING) Movement.Type type;
    @Enumerated(EnumType.STRING) Movement.Status status;
    protected MovementEntity() {}
    MovementEntity(Movement movement) {
        id = movement.id(); sku = movement.sku(); warehouse = movement.warehouse();
        quantity = movement.quantity(); type = movement.type(); status = movement.status();
    }
    Movement domain() { return new Movement(id, sku, warehouse, quantity, type, status); }
}

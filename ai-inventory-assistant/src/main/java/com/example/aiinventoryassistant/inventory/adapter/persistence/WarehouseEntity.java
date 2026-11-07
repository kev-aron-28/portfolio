package com.example.aiinventoryassistant.inventory.adapter.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouse")
public class WarehouseEntity {
    @Id
    private String code;
    private String name;

    protected WarehouseEntity() {}
    WarehouseEntity(String code, String name) { this.code = code; this.name = name; }
}

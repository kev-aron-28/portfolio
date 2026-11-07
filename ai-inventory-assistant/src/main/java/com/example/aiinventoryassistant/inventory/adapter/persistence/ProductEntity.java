package com.example.aiinventoryassistant.inventory.adapter.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity {
    @Id
    private String sku;
    private String name;

    protected ProductEntity() {}
    ProductEntity(String sku, String name) { this.sku = sku; this.name = name; }
    String sku() { return sku; }
    com.example.aiinventoryassistant.inventory.domain.Product domain() { return new com.example.aiinventoryassistant.inventory.domain.Product(sku, name); }
}

package com.example.aiinventoryassistant.inventory.adapter.persistence;

import com.example.aiinventoryassistant.inventory.domain.Stock;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock")
@IdClass(StockId.class)
public class StockEntity {
    @Id private String sku;
    @Id private String warehouse;
    private long quantity;
    protected StockEntity() {}
    StockEntity(String sku, String warehouse, long quantity) { this.sku = sku; this.warehouse = warehouse; this.quantity = quantity; }
    Stock domain() { return new Stock(sku, warehouse, quantity); }
    long quantity() { return quantity; }
    void quantity(long quantity) { this.quantity = quantity; }
}

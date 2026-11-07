package com.example.aiinventoryassistant.inventory.adapter.persistence;

import java.io.Serializable;

public class StockId implements Serializable {
    private String sku;
    private String warehouse;
    public StockId() {}
    public StockId(String sku, String warehouse) { this.sku = sku; this.warehouse = warehouse; }
    @Override public boolean equals(Object other) { return other instanceof StockId id && sku.equals(id.sku) && warehouse.equals(id.warehouse); }
    @Override public int hashCode() { return 31 * sku.hashCode() + warehouse.hashCode(); }
}

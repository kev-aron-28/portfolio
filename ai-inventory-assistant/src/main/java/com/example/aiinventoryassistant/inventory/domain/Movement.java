package com.example.aiinventoryassistant.inventory.domain;

import java.util.UUID;

public record Movement(UUID id, String sku, String warehouse, long quantity,
                       Type type, Status status) {
    public enum Type { ENTRY, EXIT }
    public enum Status { PENDING, APPLIED }
    public Movement {
        if (sku == null || sku.isBlank() || warehouse == null || warehouse.isBlank()
                || quantity <= 0 || type == null || status == null || id == null) {
            throw new IllegalArgumentException("Valid product, warehouse, type and positive quantity are required");
        }
    }
    public long applyTo(long current) {
        if (status != Status.PENDING) throw new IllegalStateException("Movement already applied");
        if (type == Type.EXIT && current < quantity) throw new IllegalStateException("Insufficient stock");
        return type == Type.ENTRY ? Math.addExact(current, quantity) : current - quantity;
    }
}

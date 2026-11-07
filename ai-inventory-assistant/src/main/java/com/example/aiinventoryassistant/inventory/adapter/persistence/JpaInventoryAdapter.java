package com.example.aiinventoryassistant.inventory.adapter.persistence;

import com.example.aiinventoryassistant.inventory.application.InventoryPort;
import com.example.aiinventoryassistant.inventory.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Repository
@Transactional
public class JpaInventoryAdapter implements InventoryPort {
    private final ProductRepository products;
    private final WarehouseRepository warehouses;
    private final StockRepository stock;
    private final MovementRepository movements;

    public JpaInventoryAdapter(ProductRepository products, WarehouseRepository warehouses, StockRepository stock, MovementRepository movements) {
        this.products = products; this.warehouses = warehouses; this.stock = stock; this.movements = movements;
    }

    public List<Product> search(String query) {
        return products.findTop100BySkuContainingIgnoreCaseOrNameContainingIgnoreCaseOrderBySku(query, query).stream().map(ProductEntity::domain).toList();
    }
    private void require(String table, String column, String value) {
        if (table.equals("product") && !products.existsById(value)) throw new NoSuchElementException("product not found");
        if (table.equals("warehouse") && !warehouses.existsById(value)) throw new NoSuchElementException("warehouse not found");
    }
    private List<Stock> stocks(String column, String value) {
        return column.equals("sku") ? stock.findBySkuOrderByWarehouse(value).stream().map(StockEntity::domain).toList() : stock.findByWarehouseOrderBySku(value).stream().map(StockEntity::domain).toList();
    }
    public List<Stock> productStock(String sku) {
        require("product", "sku", sku);
        return stocks("sku", sku);
    }
    public List<Stock> warehouseStock(String warehouse) {
        require("warehouse", "code", warehouse);
        return stocks("warehouse", warehouse);
    }
    public Movement propose(Movement movement) {
        require("product", "sku", movement.sku());
        require("warehouse", "code", movement.warehouse());
        movements.save(new MovementEntity(movement));
        return movement;
    }
    public Movement confirm(UUID id) {
        MovementEntity entity = movements.findLockedById(id);
        if (entity == null) throw new NoSuchElementException("Movement not found");
        if (entity.status == Movement.Status.APPLIED) return entity.domain();
        StockEntity current = stock.findBySkuAndWarehouse(entity.sku, entity.warehouse);
        if (current == null) current = stock.save(new StockEntity(entity.sku, entity.warehouse, 0));
        current.quantity(entity.domain().applyTo(current.quantity()));
        entity.status = Movement.Status.APPLIED;
        return entity.domain();
    }
    public Movement findMovement(UUID id) {
        MovementEntity entity = movements.findById(id).orElseThrow(() -> new NoSuchElementException("Movement not found"));
        return entity.domain();
    }

    public List<Movement> movements() {
        return movements.findAllByOrderById().stream().map(MovementEntity::domain).toList();
    }
}

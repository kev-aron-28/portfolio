package com.example.aiinventoryassistant.inventory.adapter.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.List;

public interface StockRepository extends JpaRepository<StockEntity, StockId> {
    List<StockEntity> findBySkuOrderByWarehouse(String sku);
    List<StockEntity> findByWarehouseOrderBySku(String warehouse);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    StockEntity findBySkuAndWarehouse(String sku, String warehouse);
}

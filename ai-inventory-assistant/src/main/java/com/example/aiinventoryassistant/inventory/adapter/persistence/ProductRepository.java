package com.example.aiinventoryassistant.inventory.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    List<ProductEntity> findTop100BySkuContainingIgnoreCaseOrNameContainingIgnoreCaseOrderBySku(String sku, String name);
}

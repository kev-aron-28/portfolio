package com.example.aiinventoryassistant.inventory.adapter.persistence;

import com.example.aiinventoryassistant.inventory.application.InventoryPort;
import com.example.aiinventoryassistant.inventory.domain.*;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Repository
@Transactional
public class JpaInventoryAdapter implements InventoryPort {
    @PersistenceContext private EntityManager em;

    public List<Product> search(String query) {
        List<?> rows = em.createNativeQuery("SELECT sku, name FROM product WHERE position(lower(:q) in lower(sku || ' ' || name)) > 0 ORDER BY sku LIMIT 100")
                .setParameter("q", query).getResultList();
        return rows.stream().map(value -> {
            Object[] row = (Object[]) value;
            return new Product((String) row[0], (String) row[1]);
        }).toList();
    }
    private void require(String table, String column, String value) {
        if (em.createNativeQuery("SELECT 1 FROM " + table + " WHERE " + column + " = :value")
                .setParameter("value", value).getResultList().isEmpty()) {
            throw new NoSuchElementException(table + " not found");
        }
    }
    private List<Stock> stocks(String column, String value) {
        List<?> rows = em.createNativeQuery("SELECT sku, warehouse, quantity FROM stock WHERE " + column + " = :value ORDER BY sku, warehouse")
                .setParameter("value", value).getResultList();
        return rows.stream().map(item -> {
            Object[] row = (Object[]) item;
            return new Stock((String) row[0], (String) row[1], ((Number) row[2]).longValue());
        }).toList();
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
        em.persist(new MovementEntity(movement));
        return movement;
    }
    public Movement confirm(UUID id) {
        MovementEntity entity = em.find(MovementEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (entity == null) throw new NoSuchElementException("Movement not found");
        if (entity.status == Movement.Status.APPLIED) return entity.domain();
        em.createNativeQuery("INSERT INTO stock(sku, warehouse, quantity) VALUES (:sku, :warehouse, 0) ON CONFLICT DO NOTHING")
                .setParameter("sku", entity.sku).setParameter("warehouse", entity.warehouse).executeUpdate();
        Number current = (Number) em.createNativeQuery("SELECT quantity FROM stock WHERE sku = :sku AND warehouse = :warehouse FOR UPDATE")
                .setParameter("sku", entity.sku).setParameter("warehouse", entity.warehouse).getSingleResult();
        long updated = entity.domain().applyTo(current.longValue());
        em.createNativeQuery("UPDATE stock SET quantity = :quantity WHERE sku = :sku AND warehouse = :warehouse")
                .setParameter("quantity", updated).setParameter("sku", entity.sku).setParameter("warehouse", entity.warehouse).executeUpdate();
        entity.status = Movement.Status.APPLIED;
        return entity.domain();
    }

    public List<Movement> movements() {
        return em.createQuery("select m from MovementEntity m order by m.id", MovementEntity.class)
                .getResultList().stream().map(MovementEntity::domain).toList();
    }
}

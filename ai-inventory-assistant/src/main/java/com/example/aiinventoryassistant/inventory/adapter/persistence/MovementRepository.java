package com.example.aiinventoryassistant.inventory.adapter.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.List;
import java.util.UUID;

public interface MovementRepository extends JpaRepository<MovementEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    MovementEntity findLockedById(UUID id);
    List<MovementEntity> findAllByOrderById();
}

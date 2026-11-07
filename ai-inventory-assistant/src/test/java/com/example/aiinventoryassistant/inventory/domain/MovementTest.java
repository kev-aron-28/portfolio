package com.example.aiinventoryassistant.inventory.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class MovementTest {
    private Movement movement(long quantity, Movement.Type type) {
        return new Movement(UUID.randomUUID(), "SKU", "MAIN", quantity, type, Movement.Status.PENDING);
    }

    @Test void entryAddsStock() {
        assertEquals(15, movement(5, Movement.Type.ENTRY).applyTo(10));
    }

    @Test void exitCanConsumeExactlyAvailableStock() {
        assertEquals(0, movement(10, Movement.Type.EXIT).applyTo(10));
    }

    @Test void exitRejectsInsufficientStock() {
        assertThrows(IllegalStateException.class, () -> movement(11, Movement.Type.EXIT).applyTo(10));
    }

    @Test void quantityMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> movement(0, Movement.Type.ENTRY));
    }

    @Test void entryRejectsOverflow() {
        assertThrows(ArithmeticException.class, () -> movement(1, Movement.Type.ENTRY).applyTo(Long.MAX_VALUE));
    }
}

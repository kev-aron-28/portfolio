package com.projects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projects.hashing.HashFunction;
import com.projects.hashing.SHA256;

public class SHA256Test {
    private HashFunction function;

    @BeforeEach
    public void beforeAll() {
        function = new SHA256();
    }

    @Test
    public void shouldReturnAHash() {
        String key = "test-key";
        assertNotNull(function.hash(key));
    }
}

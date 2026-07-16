package com.projects.api_service.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShortenerTest {
    private Shortener shortener;

    @BeforeEach
    void setup() {
        shortener = new Shortener();
    }

    @Test
    void shouldReturnNonEmptyHash() {
        String test = "http://google.com.mx/";

        String url = this.shortener.hash(test);
    
        assertNotNull(url);
        assertFalse(url.isEmpty());
    }

    @Test
    void shouldReturnSameHash_forSameInput() {
        String test = "http://google.com.mx/";
        
        String hash1 = this.shortener.hash(test);
        String hash2 = this.shortener.hash(test);

        assertEquals(hash1, hash2);
    }

    @Test
    void shouldReturnDifferentHash_forDifferentInput() {
        String test1 = "http://google.com.mx/";
        String test2 = "http://url.com.mx";


        String hash1 = this.shortener.hash(test1);
        String hash2 = this.shortener.hash(test2);

        assertNotEquals(hash1, hash2);
    }

    @Test
    void shouldReturnHashFixedLength8() {
        String test1 = "http://google.com.mx/";
        String test2 = "http://url.com.mx";


        String hash1 = this.shortener.hash(test1);
        String hash2 = this.shortener.hash(test2);

        assertTrue(hash1.length() == 8);
        assertTrue(hash2.length() == 8);
    }
}

package com.projects.api_service.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.projects.api_service.domain.exceptions.HashGenerationException;

@Service
public class Shortener {
    private final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private byte[] sha265(String longUrl) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            
            return algorithm.digest(longUrl.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new HashGenerationException("Error generating hash", e);
        }

    }

    private long convertHashToLong(byte[] hash) {
        long value = 0;

        for(int i = 0; i < 8; i++) {
            value = (value << 8) | (hash[i] & 0xff);
        }

        return value & Long.MAX_VALUE;
    }

    private String base62(long value) {
        StringBuilder sb = new StringBuilder();

        if (value == 0) return "0";

        while (value > 0) { 
            int index = (int) (value % 62);
            sb.append(BASE62.charAt(index));
            value /= 62;
        }

        return sb.reverse().toString();
    }

    private String base62Fixed(long value, int length) {
        String base = base62(value);

        if(base.length() < length) {
            return "0".repeat(length - base.length()) + base;
        }

        return base.substring(base.length() - length);
    }

    public String hash(String longUrl) {
        
        byte hash[] = this.sha265(longUrl);
        
        long first8Bytes = this.convertHashToLong(hash);

        String shortUrl = this.base62Fixed(first8Bytes, 8);

        return shortUrl;
    }
}

package com.projects.api_service.domain;

import java.security.MessageDigest;

import org.springframework.stereotype.Service;

@Service
public class Shortener {
    private final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private byte[] sha265(String longUrl) throws Exception {
        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");

        return algorithm.digest(longUrl.getBytes());
    }

    private long convertHashToLong(byte[] hash) {
        long value = 0;

        for(int i = 0; i < 8; i++) {
            value = (value << 8) | (hash[i] & 0xff);
        }

        return value;
    }

    private String base62(long value) {
        StringBuilder sb = new StringBuilder();

        while (value > 0) { 
            sb.append(BASE62.charAt( (int) value % 62 ));

            value /= 62;
        }

        return sb.reverse().toString();
    }

    public String hash(String longUrl) throws Exception {
        byte hash[] = this.sha265(longUrl);

        long first8Bytes = this.convertHashToLong(hash);

        String shortUrl = this.base62(first8Bytes);

        return shortUrl;
    }
}

package com.projects.hashing;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.projects.exceptions.HashingException;

public class SHA256 implements HashFunction {
    @Override
    public BigInteger hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            byte[] hash = md.digest(key.getBytes(StandardCharsets.UTF_8));

            return new BigInteger(1, hash);
        } catch (Exception e) {
            throw new HashingException();
        }
    }
}

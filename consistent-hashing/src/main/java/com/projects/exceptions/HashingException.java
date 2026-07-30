package com.projects.exceptions;

public class HashingException extends RuntimeException {

    public HashingException() {
        super("Something went wrong while hashing");
    }
    
}

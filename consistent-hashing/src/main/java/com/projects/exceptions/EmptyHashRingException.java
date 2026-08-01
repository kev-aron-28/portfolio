package com.projects.exceptions;

public class EmptyHashRingException extends RuntimeException {
    public EmptyHashRingException() {
        super("The ring is empty");
    }
}

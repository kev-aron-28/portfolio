package com.projects.domain;

public class RuleError {
    private final int line;
    private final String message;

    public RuleError(int line, String message) {
        this.line = line;
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public String getMessage() {
        return message;
    }
}

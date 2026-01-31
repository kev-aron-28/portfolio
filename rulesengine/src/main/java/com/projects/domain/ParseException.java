package com.projects.domain;

import java.util.List;

public class ParseException extends RuntimeException {
    private final List<RuleError> errors;

    public ParseException(List<RuleError> errors) {
        super("Error while parsing the rules file");

        this.errors = List.copyOf(errors);
    }

    public List<RuleError> getErrors() {
        return errors;
    }    
}

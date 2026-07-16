package com.projects.message_worker.domain.errors;

public class InvalidTemplate extends DomainException {

    public InvalidTemplate() {
        super("The template provided does not matches the sintax, must at least contain one valid place hoder {variable}");
    }
    
}

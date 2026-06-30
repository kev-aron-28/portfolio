package com.projects.message_worker.domain.errors;

public class TemplateNotFound extends DomainException {

    public TemplateNotFound(Long id) {
        super("The template with the id of " + id + " was not found");
    }
    
}

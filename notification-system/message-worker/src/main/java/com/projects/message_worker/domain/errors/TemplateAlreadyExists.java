package com.projects.message_worker.domain.errors;

public class TemplateAlreadyExists extends DomainException {

    public TemplateAlreadyExists() {
        super("Template name value must be unique");
    }
}

package com.projects.api_service.domain.errors;

public class TemplateAlreadyExists extends DomainException {

    public TemplateAlreadyExists() {
        super("Template name value must be unique");
    }
}

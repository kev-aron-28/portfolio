package com.projects.api_service.domain.errors;

public class MissingTemplateVariable extends DomainException {

    public MissingTemplateVariable(String variable) {
        super("There is a missing variable in the template: " + variable);
    }
    
}

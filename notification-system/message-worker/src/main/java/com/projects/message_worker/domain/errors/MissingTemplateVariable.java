package com.projects.message_worker.domain.errors;

public class MissingTemplateVariable extends DomainException {

    public MissingTemplateVariable(String variable) {
        super("There is a missing variable in the template: " + variable);
    }
    
}

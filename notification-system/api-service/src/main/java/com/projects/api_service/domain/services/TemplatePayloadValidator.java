package com.projects.api_service.domain.services;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.projects.api_service.domain.errors.MissingTemplateVariable;

@Service
public class TemplatePayloadValidator {
    private final TemplateVariableExtractor extractor = new TemplateVariableExtractor();

    public void validate(String template, Map<String, Object> payload) {
        Set<String> requriedFields = this.extractor.extract(template);
        
        for(String variable : requriedFields) {
            if(!payload.containsKey(variable)) {
                throw new MissingTemplateVariable(variable);
            }
        }
    }
}

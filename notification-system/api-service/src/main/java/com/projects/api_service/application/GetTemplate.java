package com.projects.api_service.application;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.projects.api_service.domain.Template;
import com.projects.api_service.domain.TemplateRepository;
import com.projects.api_service.domain.errors.TemplateNotFound;

@Service
public class GetTemplate {
    private final TemplateRepository repository;

    public GetTemplate(TemplateRepository repository) {
        this.repository = repository;
    }

    @Cacheable(
        value = "templates",
        key="#templateId"
    )
    public Template run(Long templateId) {
        return this.repository.findById(templateId)
            .orElseThrow(() -> new TemplateNotFound(templateId));
    }
}

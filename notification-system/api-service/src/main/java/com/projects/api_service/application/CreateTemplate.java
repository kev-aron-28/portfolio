package com.projects.api_service.application;

import org.springframework.stereotype.Service;

import com.projects.api_service.domain.Template;
import com.projects.api_service.domain.TemplateRepository;
import com.projects.api_service.service.api.dto.CreateTemplateDto;

@Service
public class CreateTemplate {
    private TemplateRepository repository;

    public CreateTemplate(TemplateRepository repository) {
        this.repository = repository;
    }

    public void run(CreateTemplateDto dto) {
        Template template = new Template(null, null, null, null);
        
        repository.save(template);
    }
}

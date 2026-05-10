package com.projects.api_service.domain;

import java.util.Optional;

public interface TemplateRepository {
    public void save(Template template);
    public Optional<Template> findById(Integer id); 
}

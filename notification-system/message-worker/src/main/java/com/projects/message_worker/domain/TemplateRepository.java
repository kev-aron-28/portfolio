package com.projects.message_worker.domain;

import java.util.Optional;

public interface TemplateRepository {
    public Optional<Template> findById(Long id);
}

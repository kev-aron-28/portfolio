package com.projects.knowledge_manager.tag.dto;

import java.time.Instant;

public record TagView(Long id, String name, Instant createdAt) {}

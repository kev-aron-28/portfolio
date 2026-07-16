package com.projects.knowledge_manager.topic.dto;

import java.time.Instant;

public record TopicView(
    Long id, String name, String description, String color, Instant createdAt, Instant updatedAt) {}

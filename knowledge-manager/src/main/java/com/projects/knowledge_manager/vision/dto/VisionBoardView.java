package com.projects.knowledge_manager.vision.dto;

import java.time.Instant;

public record VisionBoardView(
    Long id,
    String title,
    String description,
    String sceneJson,
    Instant createdAt,
    Instant updatedAt,
    int nodeCount) {}

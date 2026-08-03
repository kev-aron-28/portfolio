package com.projects.knowledge_manager.vision.dto;

import java.time.Instant;

public record VisionBoardSummaryView(
    Long id, String title, String description, Instant updatedAt, int nodeCount) {}

package com.projects.knowledge_manager.problem.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.tag.dto.TagView;
import java.time.Instant;
import java.util.List;

public record ProblemDetailView(
    Long id,
    String title,
    String url,
    Difficulty difficulty,
    String description,
    boolean favorite,
    boolean archived,
    Long topicId,
    String topicName,
    String topicColor,
    List<TagView> tags,
    SolutionView solution,
    Instant createdAt,
    Instant updatedAt) {}

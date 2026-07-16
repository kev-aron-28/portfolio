package com.projects.knowledge_manager.problem.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.tag.dto.TagView;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ProblemSummaryView(
    Long id,
    String title,
    String url,
    Difficulty difficulty,
    boolean favorite,
    boolean archived,
    Long topicId,
    String topicName,
    String topicColor,
    List<TagView> tags,
    LocalDate nextReviewDate,
    boolean overdue,
    Instant updatedAt) {}

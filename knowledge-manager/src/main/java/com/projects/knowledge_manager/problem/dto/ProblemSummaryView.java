package com.projects.knowledge_manager.problem.dto;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.tag.dto.TagView;
import com.projects.knowledge_manager.topic.dto.TopicRefView;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record ProblemSummaryView(
    Long id,
    String title,
    String url,
    Difficulty difficulty,
    boolean favorite,
    boolean archived,
    List<TopicRefView> topics,
    List<TagView> tags,
    LocalDate nextReviewDate,
    boolean overdue,
    Instant updatedAt) {

  public String topicName() {
    return topics.stream().map(TopicRefView::name).collect(Collectors.joining(", "));
  }

  public String topicColor() {
    return topics.isEmpty() ? "#94a3b8" : topics.getFirst().color();
  }

  public boolean belongsToTopic(Long topicId) {
    return topicId != null && topics.stream().anyMatch(topic -> topic.id().equals(topicId));
  }
}

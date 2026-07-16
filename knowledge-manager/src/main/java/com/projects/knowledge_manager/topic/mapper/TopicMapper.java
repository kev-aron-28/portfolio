package com.projects.knowledge_manager.topic.mapper;

import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.dto.TopicView;
import com.projects.knowledge_manager.topic.entity.Topic;

public final class TopicMapper {

  private TopicMapper() {}

  public static TopicView toView(Topic topic) {
    return new TopicView(
        topic.getId(),
        topic.getName(),
        topic.getDescription(),
        topic.getColor(),
        topic.getCreatedAt(),
        topic.getUpdatedAt());
  }

  public static TopicForm toForm(Topic topic) {
    return new TopicForm(topic.getName(), topic.getDescription(), topic.getColor());
  }

  public static void updateEntity(Topic topic, TopicForm form) {
    topic.setName(form.name().trim());
    topic.setDescription(normalizeDescription(form.description()));
    topic.setColor(form.color());
  }

  public static Topic toEntity(TopicForm form) {
    return new Topic(
        form.name().trim(), normalizeDescription(form.description()), form.color());
  }

  private static String normalizeDescription(String description) {
    if (description == null || description.isBlank()) {
      return null;
    }
    return description.trim();
  }
}

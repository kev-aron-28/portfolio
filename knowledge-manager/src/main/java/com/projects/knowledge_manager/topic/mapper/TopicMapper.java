package com.projects.knowledge_manager.topic.mapper;

import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.dto.TopicRefView;
import com.projects.knowledge_manager.topic.dto.TopicView;
import com.projects.knowledge_manager.topic.entity.Topic;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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

  public static TopicRefView toRefView(Topic topic) {
    return new TopicRefView(topic.getId(), topic.getName(), topic.getColor());
  }

  public static List<TopicRefView> toRefViews(Set<Topic> topics) {
    return topics.stream()
        .map(TopicMapper::toRefView)
        .sorted(Comparator.comparing(ref -> ref.name().toLowerCase()))
        .toList();
  }

  public static TopicForm toForm(Topic topic, List<Long> problemIds) {
    return new TopicForm(
        topic.getName(), topic.getDescription(), topic.getColor(), problemIds);
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

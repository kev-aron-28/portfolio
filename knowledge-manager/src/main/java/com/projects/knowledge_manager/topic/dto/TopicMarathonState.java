package com.projects.knowledge_manager.topic.dto;

import java.util.ArrayList;
import java.util.List;

/** In-progress topic marathon kept in the HTTP session. */
public class TopicMarathonState {

  private final Long topicId;
  private final String topicName;
  private final String topicColor;
  private final long startedAtEpochMs;
  private final List<TopicMarathonCompletedItem> completed = new ArrayList<>();

  public TopicMarathonState(
      Long topicId, String topicName, String topicColor, long startedAtEpochMs) {
    this.topicId = topicId;
    this.topicName = topicName;
    this.topicColor = topicColor;
    this.startedAtEpochMs = startedAtEpochMs;
  }

  public Long getTopicId() {
    return topicId;
  }

  public String getTopicName() {
    return topicName;
  }

  public String getTopicColor() {
    return topicColor;
  }

  public long getStartedAtEpochMs() {
    return startedAtEpochMs;
  }

  public List<TopicMarathonCompletedItem> getCompleted() {
    return completed;
  }

  public int completedCount() {
    return completed.size();
  }

  public int sessionMinutes() {
    return completed.stream().mapToInt(TopicMarathonCompletedItem::durationMinutes).sum();
  }

  public List<Long> completedProblemIds() {
    return completed.stream().map(TopicMarathonCompletedItem::problemId).toList();
  }
}

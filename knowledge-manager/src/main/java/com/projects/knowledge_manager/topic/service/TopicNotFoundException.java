package com.projects.knowledge_manager.topic.service;

public class TopicNotFoundException extends RuntimeException {

  public TopicNotFoundException(Long id) {
    super("Topic not found: " + id);
  }
}

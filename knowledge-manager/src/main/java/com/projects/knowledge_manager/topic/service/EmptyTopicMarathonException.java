package com.projects.knowledge_manager.topic.service;

public class EmptyTopicMarathonException extends RuntimeException {

  public EmptyTopicMarathonException(String topicName) {
    super("Topic \"" + topicName + "\" has no active problems to practice.");
  }
}

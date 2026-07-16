package com.projects.knowledge_manager.topic.service;

public class DuplicateTopicNameException extends RuntimeException {

  public DuplicateTopicNameException(String name) {
    super("A topic named '" + name + "' already exists");
  }
}

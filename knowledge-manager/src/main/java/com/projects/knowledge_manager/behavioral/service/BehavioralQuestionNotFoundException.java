package com.projects.knowledge_manager.behavioral.service;

public class BehavioralQuestionNotFoundException extends RuntimeException {

  public BehavioralQuestionNotFoundException(Long id) {
    super("Behavioral question not found: " + id);
  }
}

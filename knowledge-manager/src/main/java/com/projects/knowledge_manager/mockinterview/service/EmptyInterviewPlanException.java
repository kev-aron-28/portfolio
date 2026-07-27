package com.projects.knowledge_manager.mockinterview.service;

public class EmptyInterviewPlanException extends RuntimeException {

  public EmptyInterviewPlanException() {
    super("Not enough algorithm or behavioral questions available to start an interview.");
  }
}

package com.projects.knowledge_manager.problem.service;

public class BulkProblemValidationException extends RuntimeException {

  private final String field;

  public BulkProblemValidationException(String field, String message) {
    super(message);
    this.field = field;
  }

  public String field() {
    return field;
  }
}

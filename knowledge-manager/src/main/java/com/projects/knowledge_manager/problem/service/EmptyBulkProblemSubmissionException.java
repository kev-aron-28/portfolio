package com.projects.knowledge_manager.problem.service;

public class EmptyBulkProblemSubmissionException extends RuntimeException {

  public EmptyBulkProblemSubmissionException() {
    super("Add at least one problem with a title");
  }
}

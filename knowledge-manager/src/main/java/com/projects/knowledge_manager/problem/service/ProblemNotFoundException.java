package com.projects.knowledge_manager.problem.service;

public class ProblemNotFoundException extends RuntimeException {

  public ProblemNotFoundException(Long id) {
    super("Problem not found: " + id);
  }
}

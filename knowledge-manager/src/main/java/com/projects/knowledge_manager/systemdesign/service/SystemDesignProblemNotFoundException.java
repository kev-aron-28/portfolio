package com.projects.knowledge_manager.systemdesign.service;

public class SystemDesignProblemNotFoundException extends RuntimeException {

  public SystemDesignProblemNotFoundException(Long id) {
    super("System design problem not found: " + id);
  }
}

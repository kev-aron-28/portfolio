package com.projects.knowledge_manager.problem.service;

public class InvalidTagSelectionException extends RuntimeException {

  public InvalidTagSelectionException() {
    super("One or more selected tags are invalid");
  }
}

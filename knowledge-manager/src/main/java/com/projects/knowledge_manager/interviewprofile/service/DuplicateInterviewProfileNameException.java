package com.projects.knowledge_manager.interviewprofile.service;

public class DuplicateInterviewProfileNameException extends RuntimeException {

  public DuplicateInterviewProfileNameException(String name) {
    super("An interview profile named \"" + name + "\" already exists.");
  }
}

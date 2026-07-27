package com.projects.knowledge_manager.interviewprofile.service;

public class InterviewProfileNotFoundException extends RuntimeException {

  public InterviewProfileNotFoundException(Long id) {
    super("Interview profile not found: " + id);
  }
}

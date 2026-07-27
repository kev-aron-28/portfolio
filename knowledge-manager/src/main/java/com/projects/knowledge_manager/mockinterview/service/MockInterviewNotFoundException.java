package com.projects.knowledge_manager.mockinterview.service;

public class MockInterviewNotFoundException extends RuntimeException {

  public MockInterviewNotFoundException(Long id) {
    super("Mock interview not found: " + id);
  }
}

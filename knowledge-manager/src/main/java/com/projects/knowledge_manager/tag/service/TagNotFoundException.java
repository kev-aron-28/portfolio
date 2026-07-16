package com.projects.knowledge_manager.tag.service;

public class TagNotFoundException extends RuntimeException {

  public TagNotFoundException(Long id) {
    super("Tag not found: " + id);
  }
}

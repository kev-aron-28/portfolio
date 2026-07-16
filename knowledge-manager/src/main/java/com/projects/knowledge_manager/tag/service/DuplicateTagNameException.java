package com.projects.knowledge_manager.tag.service;

public class DuplicateTagNameException extends RuntimeException {

  public DuplicateTagNameException(String name) {
    super("A tag named '" + name + "' already exists");
  }
}

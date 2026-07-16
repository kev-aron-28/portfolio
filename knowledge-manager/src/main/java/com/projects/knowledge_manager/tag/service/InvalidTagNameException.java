package com.projects.knowledge_manager.tag.service;

public class InvalidTagNameException extends RuntimeException {

  public InvalidTagNameException(String name) {
    super("Tag name is invalid: " + name);
  }
}

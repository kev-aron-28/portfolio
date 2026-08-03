package com.projects.knowledge_manager.vision.service;

public class VisionBoardNotFoundException extends RuntimeException {

  public VisionBoardNotFoundException(Long id) {
    super("Vision board not found: " + id);
  }
}

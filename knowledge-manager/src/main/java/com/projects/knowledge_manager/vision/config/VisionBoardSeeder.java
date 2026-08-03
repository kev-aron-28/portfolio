package com.projects.knowledge_manager.vision.config;

import com.projects.knowledge_manager.vision.service.VisionBoardService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class VisionBoardSeeder implements ApplicationRunner {

  private final VisionBoardService visionBoardService;

  public VisionBoardSeeder(VisionBoardService visionBoardService) {
    this.visionBoardService = visionBoardService;
  }

  @Override
  public void run(ApplicationArguments args) {
    visionBoardService.ensureDemoBoardExists();
  }
}

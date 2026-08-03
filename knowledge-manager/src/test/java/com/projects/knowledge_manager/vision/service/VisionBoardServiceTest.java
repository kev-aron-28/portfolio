package com.projects.knowledge_manager.vision.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.projects.knowledge_manager.vision.dto.VisionBoardForm;
import com.projects.knowledge_manager.vision.repository.VisionBoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class VisionBoardServiceTest {

  @Autowired private VisionBoardService visionBoardService;
  @Autowired private VisionBoardRepository visionBoardRepository;

  @Test
  void createWithDemoTemplateLoadsRichScene() {
    var created =
        visionBoardService.create(new VisionBoardForm("My roadmap", "Horizon", true));

    assertThat(created.title()).isEqualTo("My roadmap");
    assertThat(created.sceneJson()).contains("Become a Senior Java Engineer");
    assertThat(created.sceneJson()).contains("\"type\": \"skill\"");
    assertThat(created.nodeCount()).isGreaterThan(10);
  }

  @Test
  void createBlankStartsEmpty() {
    var created = visionBoardService.create(new VisionBoardForm("Blank", null, false));

    assertThat(created.sceneJson()).contains("\"nodes\":[]");
    assertThat(created.nodeCount()).isZero();
  }

  @Test
  void ensureDemoBoardDoesNotDuplicate() {
    long before = visionBoardRepository.count();
    visionBoardService.ensureDemoBoardExists();
    visionBoardService.ensureDemoBoardExists();
    assertThat(visionBoardRepository.count()).isEqualTo(before);
  }

  @Test
  void saveSceneRejectsOversizedPayload() {
    var created = visionBoardService.create(new VisionBoardForm("Board", null, false));
    String huge = "x".repeat(2_000_001);

    assertThatThrownBy(() -> visionBoardService.saveScene(created.id(), huge))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("too large");
  }

  @Test
  void findMissingThrows() {
    assertThatThrownBy(() -> visionBoardService.findById(9999L))
        .isInstanceOf(VisionBoardNotFoundException.class);
  }
}

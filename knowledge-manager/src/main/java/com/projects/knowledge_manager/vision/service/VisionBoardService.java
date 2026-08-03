package com.projects.knowledge_manager.vision.service;

import com.projects.knowledge_manager.vision.dto.VisionBoardForm;
import com.projects.knowledge_manager.vision.dto.VisionBoardSummaryView;
import com.projects.knowledge_manager.vision.dto.VisionBoardView;
import com.projects.knowledge_manager.vision.entity.VisionBoard;
import com.projects.knowledge_manager.vision.mapper.VisionBoardMapper;
import com.projects.knowledge_manager.vision.repository.VisionBoardRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VisionBoardService {

  private static final int MAX_SCENE_CHARS = 2_000_000;
  private static final String EMPTY_SCENE =
      """
      {"version":1,"viewport":{"x":0,"y":0,"zoom":1},"timeline":{"start":"2026-01-01","end":"2036-01-01","unit":"day","y":80},"bookmarks":[],"nodes":[],"edges":[]}
      """
          .trim();

  private final VisionBoardRepository visionBoardRepository;
  private final String demoSceneJson;

  public VisionBoardService(VisionBoardRepository visionBoardRepository) {
    this.visionBoardRepository = visionBoardRepository;
    this.demoSceneJson = loadDemoScene();
  }

  public List<VisionBoardSummaryView> findAll() {
    return visionBoardRepository.findAllByOrderByUpdatedAtDesc().stream()
        .map(VisionBoardMapper::toSummary)
        .toList();
  }

  public VisionBoardView findById(Long id) {
    return VisionBoardMapper.toView(getBoardOrThrow(id));
  }

  public VisionBoardForm findFormById(Long id) {
    return VisionBoardMapper.toForm(getBoardOrThrow(id));
  }

  @Transactional
  public VisionBoardView create(VisionBoardForm form) {
    String scene = form.useDemoTemplate() ? demoSceneJson : EMPTY_SCENE;
    VisionBoard saved = visionBoardRepository.save(VisionBoardMapper.toEntity(form, scene));
    return VisionBoardMapper.toView(saved);
  }

  @Transactional
  public VisionBoardView update(Long id, VisionBoardForm form) {
    VisionBoard board = getBoardOrThrow(id);
    VisionBoardMapper.updateEntity(board, form);
    return VisionBoardMapper.toView(board);
  }

  @Transactional
  public void delete(Long id) {
    visionBoardRepository.delete(getBoardOrThrow(id));
  }

  @Transactional
  public void saveScene(Long id, String sceneJson) {
    VisionBoard board = getBoardOrThrow(id);
    if (sceneJson != null && sceneJson.length() > MAX_SCENE_CHARS) {
      throw new IllegalArgumentException("Vision scene is too large.");
    }
    board.setSceneJson(sceneJson == null || sceneJson.isBlank() ? EMPTY_SCENE : sceneJson);
  }

  @Transactional
  public void ensureDemoBoardExists() {
    if (visionBoardRepository.count() > 0) {
      return;
    }
    visionBoardRepository.save(
        new VisionBoard(
            "Become who I am building",
            "The roadmap of the person I want to become.",
            demoSceneJson));
  }

  public String getDemoSceneJson() {
    return demoSceneJson;
  }

  private VisionBoard getBoardOrThrow(Long id) {
    return visionBoardRepository.findById(id).orElseThrow(() -> new VisionBoardNotFoundException(id));
  }

  private static String loadDemoScene() {
    ClassPathResource resource = new ClassPathResource("vision/demo-scene.json");
    try (InputStream in = resource.getInputStream()) {
      return new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to load vision demo scene", exception);
    }
  }
}

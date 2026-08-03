package com.projects.knowledge_manager.vision.mapper;

import com.projects.knowledge_manager.vision.dto.VisionBoardForm;
import com.projects.knowledge_manager.vision.dto.VisionBoardSummaryView;
import com.projects.knowledge_manager.vision.dto.VisionBoardView;
import com.projects.knowledge_manager.vision.entity.VisionBoard;

public final class VisionBoardMapper {

  private VisionBoardMapper() {}

  public static VisionBoardView toView(VisionBoard board) {
    return new VisionBoardView(
        board.getId(),
        board.getTitle(),
        board.getDescription(),
        board.getSceneJson(),
        board.getCreatedAt(),
        board.getUpdatedAt(),
        countNodes(board.getSceneJson()));
  }

  public static VisionBoardSummaryView toSummary(VisionBoard board) {
    return new VisionBoardSummaryView(
        board.getId(),
        board.getTitle(),
        board.getDescription(),
        board.getUpdatedAt(),
        countNodes(board.getSceneJson()));
  }

  public static VisionBoardForm toForm(VisionBoard board) {
    return new VisionBoardForm(board.getTitle(), nullToEmpty(board.getDescription()), false);
  }

  public static VisionBoard toEntity(VisionBoardForm form, String sceneJson) {
    String description = form.description() == null ? null : form.description().trim();
    if (description != null && description.isEmpty()) {
      description = null;
    }
    return new VisionBoard(form.title().trim(), description, sceneJson);
  }

  public static void updateEntity(VisionBoard board, VisionBoardForm form) {
    board.setTitle(form.title().trim());
    String description = form.description() == null ? null : form.description().trim();
    board.setDescription(description == null || description.isEmpty() ? null : description);
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }

  private static int countNodes(String sceneJson) {
    if (sceneJson == null || sceneJson.isBlank()) {
      return 0;
    }
    int marker = sceneJson.indexOf("\"nodes\"");
    if (marker < 0) {
      return 0;
    }
    int count = 0;
    int from = marker;
    while (true) {
      int next = sceneJson.indexOf("\"id\"", from);
      if (next < 0) {
        break;
      }
      // Rough count: ids after nodes array; edges also have id — prefer "type" as node marker
      from = next + 4;
    }
    from = marker;
    while (true) {
      int next = sceneJson.indexOf("\"type\"", from);
      if (next < 0) {
        break;
      }
      count++;
      from = next + 6;
    }
    return count;
  }
}

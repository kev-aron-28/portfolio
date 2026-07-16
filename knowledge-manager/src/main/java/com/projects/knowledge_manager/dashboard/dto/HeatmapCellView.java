package com.projects.knowledge_manager.dashboard.dto;

import java.time.LocalDate;

public record HeatmapCellView(LocalDate date, long count) {

  public boolean isEmpty() {
    return date == null;
  }

  public int level() {
    if (count <= 0) {
      return 0;
    }
    if (count == 1) {
      return 1;
    }
    if (count == 2) {
      return 2;
    }
    if (count == 3) {
      return 3;
    }
    return 4;
  }

  public static HeatmapCellView empty() {
    return new HeatmapCellView(null, 0);
  }
}

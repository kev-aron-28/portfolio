package com.projects.knowledge_manager.systemdesign.model;

public enum ReviewStatusFilter {
  ALL("All"),
  DUE_TODAY("Due Today"),
  OVERDUE("Overdue"),
  UPCOMING("Upcoming"),
  NEVER_REVIEWED("Never Reviewed"),
  MASTERED("Mastered");

  private final String label;

  ReviewStatusFilter(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}

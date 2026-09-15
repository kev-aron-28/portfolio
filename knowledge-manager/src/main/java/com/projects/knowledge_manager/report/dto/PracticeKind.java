package com.projects.knowledge_manager.report.dto;

public enum PracticeKind {
  ALGORITHM("Algorithms", "bi-puzzle", "algorithm"),
  BEHAVIORAL("Behavioral", "bi-chat-quote", "behavioral"),
  SYSTEM_DESIGN("System Design", "bi-diagram-3", "system-design"),
  MOCK_INTERVIEW("Mock Interview", "bi-briefcase", "mock");

  private final String label;
  private final String icon;
  private final String cssClass;

  PracticeKind(String label, String icon, String cssClass) {
    this.label = label;
    this.icon = icon;
    this.cssClass = cssClass;
  }

  public String getLabel() {
    return label;
  }

  public String getIcon() {
    return icon;
  }

  public String getCssClass() {
    return cssClass;
  }
}

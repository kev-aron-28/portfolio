package com.projects.knowledge_manager.mockinterview.model;

/** Named interview formats. New formats plug in via InterviewGenerator implementations. */
public enum InterviewFormat {
  STANDARD_MIX("Standard Mix", "Behavioral + algorithms in a short realistic loop"),
  FULL_MIX("Full Mix", "Behavioral, algorithms, and system design"),
  ALGORITHMS_ONLY("Algorithms Only", "Focus on coding problems"),
  BEHAVIORAL_ONLY("Behavioral Only", "Focus on STAR stories"),
  SYSTEM_DESIGN_ONLY("System Design Only", "Focus on architecture questions"),
  PROFILE_DRIVEN("Profile Interview", "Configured by the selected interview profile");

  private final String label;
  private final String description;

  InterviewFormat(String label, String description) {
    this.label = label;
    this.description = description;
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }
}

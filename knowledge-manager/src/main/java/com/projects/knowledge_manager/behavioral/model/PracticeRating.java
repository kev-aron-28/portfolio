package com.projects.knowledge_manager.behavioral.model;

/** Maps practice UI labels to SM-2 quality (1–5). Hard maps to 3 so it still counts as a pass. */
public enum PracticeRating {
  FORGOT(1, "Forgot"),
  HARD(3, "Hard"),
  GOOD(4, "Good"),
  EASY(5, "Easy");

  private final int quality;
  private final String label;

  PracticeRating(int quality, String label) {
    this.quality = quality;
    this.label = label;
  }

  public int getQuality() {
    return quality;
  }

  public String getLabel() {
    return label;
  }

  public static PracticeRating fromQuality(int quality) {
    for (PracticeRating rating : values()) {
      if (rating.quality == quality) {
        return rating;
      }
    }
    return null;
  }
}

package com.projects.knowledge_manager.systemdesign.model;

public enum SystemDesignCategory {
  SOCIAL_NETWORKS("Social Networks"),
  MESSAGING("Messaging"),
  STORAGE("Storage"),
  CACHING("Caching"),
  CDN("CDN"),
  STREAMING("Streaming"),
  PAYMENTS("Payments"),
  DATABASES("Databases"),
  DISTRIBUTED_SYSTEMS("Distributed Systems"),
  SEARCH("Search"),
  ANALYTICS("Analytics"),
  SCHEDULING("Scheduling"),
  SECURITY("Security"),
  NOTIFICATIONS("Notifications"),
  INFRASTRUCTURE("Infrastructure");

  private final String label;

  SystemDesignCategory(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}

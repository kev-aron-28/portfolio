package com.projects.knowledge_manager.behavioral.model;

public enum BehavioralCategory {
  LEADERSHIP("Leadership"),
  CONFLICT_RESOLUTION("Conflict Resolution"),
  COMMUNICATION("Communication"),
  FAILURE("Failure"),
  SUCCESS("Success"),
  TEAMWORK("Teamwork"),
  PROBLEM_SOLVING("Problem Solving"),
  ADAPTABILITY("Adaptability"),
  OWNERSHIP("Ownership"),
  TIME_MANAGEMENT("Time Management"),
  DECISION_MAKING("Decision Making"),
  LEARNING("Learning"),
  CUSTOMER_FOCUS("Customer Focus");

  private final String label;

  BehavioralCategory(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}

package com.projects.knowledge_manager.problem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "solutions")
public class Solution {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "problem_id", nullable = false, unique = true)
  private Problem problem;

  @Column(nullable = false, length = 50)
  private String language;

  @Column(name = "source_code", columnDefinition = "TEXT")
  private String sourceCode;

  @Column(columnDefinition = "TEXT")
  private String explanation;

  @Column(columnDefinition = "TEXT")
  private String complexity;

  @Column(columnDefinition = "TEXT")
  private String mistakes;

  protected Solution() {}

  public Solution(Problem problem) {
    this.problem = problem;
    this.language = "java";
  }

  public Long getId() {
    return id;
  }

  public Problem getProblem() {
    return problem;
  }

  public void setProblem(Problem problem) {
    this.problem = problem;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public String getComplexity() {
    return complexity;
  }

  public void setComplexity(String complexity) {
    this.complexity = complexity;
  }

  public String getMistakes() {
    return mistakes;
  }

  public void setMistakes(String mistakes) {
    this.mistakes = mistakes;
  }
}

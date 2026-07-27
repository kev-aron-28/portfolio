package com.projects.knowledge_manager.systemdesign.entity;

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
@Table(name = "system_design_documents")
public class SystemDesignDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "problem_id", nullable = false, unique = true)
  private SystemDesignProblem problem;

  @Column(columnDefinition = "TEXT")
  private String overview;

  @Column(name = "functional_requirements", columnDefinition = "TEXT")
  private String functionalRequirements;

  @Column(name = "non_functional_requirements", columnDefinition = "TEXT")
  private String nonFunctionalRequirements;

  @Column(columnDefinition = "TEXT")
  private String assumptions;

  @Column(name = "high_level_architecture", columnDefinition = "TEXT")
  private String highLevelArchitecture;

  @Column(columnDefinition = "TEXT")
  private String components;

  @Column(name = "database_design", columnDefinition = "TEXT")
  private String databaseDesign;

  @Column(name = "api_design", columnDefinition = "TEXT")
  private String apiDesign;

  @Column(name = "scaling_strategy", columnDefinition = "TEXT")
  private String scalingStrategy;

  @Column(name = "caching_strategy", columnDefinition = "TEXT")
  private String cachingStrategy;

  @Column(name = "load_balancing", columnDefinition = "TEXT")
  private String loadBalancing;

  @Column(columnDefinition = "TEXT")
  private String messaging;

  @Column(columnDefinition = "TEXT")
  private String tradeoffs;

  @Column(columnDefinition = "TEXT")
  private String bottlenecks;

  @Column(name = "lessons_learned", columnDefinition = "TEXT")
  private String lessonsLearned;

  @Column(name = "personal_notes", columnDefinition = "TEXT")
  private String personalNotes;

  protected SystemDesignDocument() {}

  public SystemDesignDocument(SystemDesignProblem problem) {
    this.problem = problem;
  }

  public Long getId() {
    return id;
  }

  public SystemDesignProblem getProblem() {
    return problem;
  }

  public void setProblem(SystemDesignProblem problem) {
    this.problem = problem;
  }

  public String getOverview() {
    return overview;
  }

  public void setOverview(String overview) {
    this.overview = overview;
  }

  public String getFunctionalRequirements() {
    return functionalRequirements;
  }

  public void setFunctionalRequirements(String functionalRequirements) {
    this.functionalRequirements = functionalRequirements;
  }

  public String getNonFunctionalRequirements() {
    return nonFunctionalRequirements;
  }

  public void setNonFunctionalRequirements(String nonFunctionalRequirements) {
    this.nonFunctionalRequirements = nonFunctionalRequirements;
  }

  public String getAssumptions() {
    return assumptions;
  }

  public void setAssumptions(String assumptions) {
    this.assumptions = assumptions;
  }

  public String getHighLevelArchitecture() {
    return highLevelArchitecture;
  }

  public void setHighLevelArchitecture(String highLevelArchitecture) {
    this.highLevelArchitecture = highLevelArchitecture;
  }

  public String getComponents() {
    return components;
  }

  public void setComponents(String components) {
    this.components = components;
  }

  public String getDatabaseDesign() {
    return databaseDesign;
  }

  public void setDatabaseDesign(String databaseDesign) {
    this.databaseDesign = databaseDesign;
  }

  public String getApiDesign() {
    return apiDesign;
  }

  public void setApiDesign(String apiDesign) {
    this.apiDesign = apiDesign;
  }

  public String getScalingStrategy() {
    return scalingStrategy;
  }

  public void setScalingStrategy(String scalingStrategy) {
    this.scalingStrategy = scalingStrategy;
  }

  public String getCachingStrategy() {
    return cachingStrategy;
  }

  public void setCachingStrategy(String cachingStrategy) {
    this.cachingStrategy = cachingStrategy;
  }

  public String getLoadBalancing() {
    return loadBalancing;
  }

  public void setLoadBalancing(String loadBalancing) {
    this.loadBalancing = loadBalancing;
  }

  public String getMessaging() {
    return messaging;
  }

  public void setMessaging(String messaging) {
    this.messaging = messaging;
  }

  public String getTradeoffs() {
    return tradeoffs;
  }

  public void setTradeoffs(String tradeoffs) {
    this.tradeoffs = tradeoffs;
  }

  public String getBottlenecks() {
    return bottlenecks;
  }

  public void setBottlenecks(String bottlenecks) {
    this.bottlenecks = bottlenecks;
  }

  public String getLessonsLearned() {
    return lessonsLearned;
  }

  public void setLessonsLearned(String lessonsLearned) {
    this.lessonsLearned = lessonsLearned;
  }

  public String getPersonalNotes() {
    return personalNotes;
  }

  public void setPersonalNotes(String personalNotes) {
    this.personalNotes = personalNotes;
  }
}

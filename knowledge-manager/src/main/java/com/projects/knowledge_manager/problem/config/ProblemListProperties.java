package com.projects.knowledge_manager.problem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.problems")
public class ProblemListProperties {

  private int pageSize = 24;

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
}

package com.projects.knowledge_manager.review.config;

import java.time.ZoneId;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.review")
public class ReviewSchedulingProperties {

  private int newProblemGraceDays = 1;
  private int newProblemSpreadDays = 30;
  private int upcomingWindowDays = 7;
  private String timeZone = "America/Mexico_City";
  private final Dashboard dashboard = new Dashboard();

  public int getNewProblemGraceDays() {
    return newProblemGraceDays;
  }

  public void setNewProblemGraceDays(int newProblemGraceDays) {
    this.newProblemGraceDays = newProblemGraceDays;
  }

  public int getNewProblemSpreadDays() {
    return newProblemSpreadDays;
  }

  public void setNewProblemSpreadDays(int newProblemSpreadDays) {
    this.newProblemSpreadDays = newProblemSpreadDays;
  }

  public int getUpcomingWindowDays() {
    return upcomingWindowDays;
  }

  public void setUpcomingWindowDays(int upcomingWindowDays) {
    this.upcomingWindowDays = upcomingWindowDays;
  }

  public Dashboard getDashboard() {
    return dashboard;
  }

  public int getDashboardDueTodayLimit() {
    return dashboard.getDueTodayLimit();
  }

  public int getDashboardUpcomingLimit() {
    return dashboard.getUpcomingLimit();
  }

  public int getDashboardNewQueueLimit() {
    return dashboard.getNewQueueLimit();
  }

  public ZoneId getZoneId() {
    return ZoneId.of(timeZone);
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public static class Dashboard {

    /** Max problems shown in today's study queue (backlog beyond this stays deferred). */
    private int dueTodayLimit = 10;

    private int upcomingLimit = 20;
    private int newQueueLimit = 10;

    public int getDueTodayLimit() {
      return dueTodayLimit;
    }

    public void setDueTodayLimit(int dueTodayLimit) {
      this.dueTodayLimit = dueTodayLimit;
    }

    public int getUpcomingLimit() {
      return upcomingLimit;
    }

    public void setUpcomingLimit(int upcomingLimit) {
      this.upcomingLimit = upcomingLimit;
    }

    public int getNewQueueLimit() {
      return newQueueLimit;
    }

    public void setNewQueueLimit(int newQueueLimit) {
      this.newQueueLimit = newQueueLimit;
    }
  }
}

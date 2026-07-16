package com.projects.knowledge_manager.review.scheduler;

import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.review.config.ReviewSchedulingProperties;
import java.time.Instant;
import java.time.LocalDate;

public class FirstReviewScheduleCalculator {

  private final ReviewSchedulingProperties properties;

  public FirstReviewScheduleCalculator(ReviewSchedulingProperties properties) {
    this.properties = properties;
  }

  public LocalDate calculateFirstReviewDate(Problem problem) {
    LocalDate registeredOn = toLocalDate(problem.getCreatedAt());
    int spreadDays = Math.max(1, properties.getNewProblemSpreadDays());
    int offset = (int) Math.floorMod(problem.getId(), spreadDays);
    return registeredOn.plusDays(properties.getNewProblemGraceDays() + offset);
  }

  private LocalDate toLocalDate(Instant instant) {
    return instant.atZone(properties.getZoneId()).toLocalDate();
  }
}

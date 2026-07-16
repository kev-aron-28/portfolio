package com.projects.knowledge_manager.review.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class FixedIntervalReviewSchedulerTest {

  private final FixedIntervalReviewScheduler scheduler = new FixedIntervalReviewScheduler();

  @Test
  void calculatesLongerIntervalsForHigherRatings() {
    LocalDate reviewDate = LocalDate.of(2026, 1, 1);

    assertThat(scheduler.calculateNextReview(new ReviewResult(reviewDate, 2), List.of()))
        .isEqualTo(reviewDate.plusDays(1));
    assertThat(scheduler.calculateNextReview(new ReviewResult(reviewDate, 4), List.of()))
        .isEqualTo(reviewDate.plusDays(7));
    assertThat(scheduler.calculateNextReview(new ReviewResult(reviewDate, 5), List.of()))
        .isEqualTo(reviewDate.plusDays(14));
  }
}

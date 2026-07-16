package com.projects.knowledge_manager.review.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class Sm2ReviewSchedulerTest {

  private final Sm2ReviewScheduler scheduler = new Sm2ReviewScheduler();

  @Test
  void increasesIntervalAfterSuccessfulReviews() {
    LocalDate firstReview = LocalDate.of(2026, 1, 1);
    LocalDate secondReview = firstReview.plusDays(1);

    LocalDate firstNext = scheduler.calculateNextReview(new ReviewResult(firstReview, 4), List.of());
    LocalDate secondNext =
        scheduler.calculateNextReview(
            new ReviewResult(secondReview, 4),
            List.of(new ReviewHistoryEntry(firstReview, 4, firstNext)));

    assertThat(firstNext).isEqualTo(firstReview.plusDays(1));
    assertThat(secondNext).isEqualTo(secondReview.plusDays(6));
  }

  @Test
  void resetsIntervalAfterLowRating() {
    LocalDate reviewDate = LocalDate.of(2026, 1, 10);
    List<ReviewHistoryEntry> history =
        List.of(
            new ReviewHistoryEntry(LocalDate.of(2026, 1, 1), 4, LocalDate.of(2026, 1, 2)),
            new ReviewHistoryEntry(LocalDate.of(2026, 1, 2), 4, LocalDate.of(2026, 1, 8)));

    LocalDate next =
        scheduler.calculateNextReview(new ReviewResult(reviewDate, 2), history);

    assertThat(next).isEqualTo(reviewDate.plusDays(1));
  }
}

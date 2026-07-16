package com.projects.knowledge_manager.review.scheduler;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class Sm2ReviewScheduler implements ReviewScheduler {

  private static final double MIN_EASINESS = 1.3;
  private static final double INITIAL_EASINESS = 2.5;
  private static final int PASSING_QUALITY = 3;

  @Override
  public LocalDate calculateNextReview(ReviewResult result, List<ReviewHistoryEntry> history) {
    SchedulerState state = new SchedulerState();

    history.stream()
        .sorted(Comparator.comparing(ReviewHistoryEntry::reviewDate))
        .forEach(entry -> state.apply(entry.rating()));

    state.apply(result.rating());
    return result.reviewDate().plusDays(state.intervalDays);
  }

  private static final class SchedulerState {
    private double easiness = INITIAL_EASINESS;
    private int repetitions;
    private int intervalDays = 1;

    private void apply(int quality) {
      if (quality >= PASSING_QUALITY) {
        intervalDays =
            switch (repetitions) {
              case 0 -> 1;
              case 1 -> 6;
              default -> Math.max(1, (int) Math.round(intervalDays * easiness));
            };
        repetitions++;
      } else {
        repetitions = 0;
        intervalDays = 1;
      }

      easiness =
          easiness + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
      if (easiness < MIN_EASINESS) {
        easiness = MIN_EASINESS;
      }
    }
  }
}

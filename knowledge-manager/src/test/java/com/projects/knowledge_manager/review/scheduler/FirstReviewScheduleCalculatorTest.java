package com.projects.knowledge_manager.review.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.review.config.ReviewSchedulingProperties;
import com.projects.knowledge_manager.topic.entity.Topic;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FirstReviewScheduleCalculatorTest {

  private FirstReviewScheduleCalculator calculator;
  private Topic topic;

  @BeforeEach
  void setUp() {
    ReviewSchedulingProperties properties = new ReviewSchedulingProperties();
    properties.setNewProblemGraceDays(1);
    properties.setNewProblemSpreadDays(30);
    properties.setTimeZone("UTC");
    calculator = new FirstReviewScheduleCalculator(properties);
    topic = new Topic("Arrays", null, "#ef4444");
  }

  @Test
  void spreadsFirstReviewDatesAcrossWindow() {
    LocalDate registeredOn = LocalDate.of(2026, 7, 8);
    Instant createdAt = registeredOn.atStartOfDay(ZoneId.of("UTC")).toInstant();

    Problem problemA = new Problem("A", Difficulty.EASY, topic);
    setProblemIdAndCreatedAt(problemA, 1L, createdAt);
    Problem problemB = new Problem("B", Difficulty.EASY, topic);
    setProblemIdAndCreatedAt(problemB, 16L, createdAt);

    assertThat(calculator.calculateFirstReviewDate(problemA)).isEqualTo(registeredOn.plusDays(2));
    assertThat(calculator.calculateFirstReviewDate(problemB)).isEqualTo(registeredOn.plusDays(17));
  }

  private void setProblemIdAndCreatedAt(Problem problem, Long id, Instant createdAt) {
    try {
      var idField = Problem.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(problem, id);

      var createdField = Problem.class.getDeclaredField("createdAt");
      createdField.setAccessible(true);
      createdField.set(problem, createdAt);
    } catch (ReflectiveOperationException exception) {
      throw new IllegalStateException(exception);
    }
  }
}

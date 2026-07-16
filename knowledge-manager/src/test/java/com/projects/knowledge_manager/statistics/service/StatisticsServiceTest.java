package com.projects.knowledge_manager.statistics.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.review.dto.ReviewForm;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.service.TopicService;
import com.projects.knowledge_manager.problem.service.ProblemService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StatisticsServiceTest {

  @Autowired private StatisticsService statisticsService;
  @Autowired private ProblemService problemService;
  @Autowired private ReviewService reviewService;
  @Autowired private TopicService topicService;

  @Test
  void buildsStatisticsAfterReviews() {
    Long topicId = topicService.create(new TopicForm("DP", null, "#22c55e")).id();
    Long problemId =
        problemService
            .create(
                new ProblemForm(
                    "Climbing Stairs",
                    "",
                    Difficulty.EASY,
                    "",
                    topicId,
                    List.of(),
                    "",
                    false,
                    false,
                    "java",
                    "",
                    "",
                    "",
                    ""))
            .id();

    reviewService.create(problemId, new ReviewForm(LocalDate.now(), 5, 20, "Easy"));
    reviewService.create(problemId, new ReviewForm(LocalDate.now(), 5, 15, "Still easy"));

    var stats = statisticsService.buildStatistics();

    assertThat(stats.totalReviewTimeMinutes()).isEqualTo(35);
    assertThat(stats.ratingStats()).hasSize(5);
    assertThat(stats.monthlyStats()).isNotEmpty();
    assertThat(stats.masteredProblems()).isEqualTo(1);
  }
}

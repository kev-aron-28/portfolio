package com.projects.knowledge_manager.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.review.dto.ReviewForm;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.service.TopicService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DashboardServiceTest {

  @Autowired private DashboardService dashboardService;
  @Autowired private ProblemService problemService;
  @Autowired private ReviewService reviewService;
  @Autowired private TopicService topicService;

  private Long problemId;

  @BeforeEach
  void setUp() {
    Long topicId = topicService.create(new TopicForm("Arrays", null, "#ef4444")).id();
    problemId =
        problemService
            .create(
                new ProblemForm(
                    "Two Sum",
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
  }

  @Test
  void showsDueProblemsAndRecentReviews() {
    var beforeReview = dashboardService.buildDashboard();
    assertThat(beforeReview.dueToday()).isEmpty();
    assertThat(beforeReview.stats().neverReviewedCount()).isEqualTo(1);
    assertThat(beforeReview.newQueueTotal()).isGreaterThanOrEqualTo(0);
    assertThat(beforeReview.dueTodayLimit()).isPositive();
    assertThat(beforeReview.dueTodayBacklog()).isZero();

    reviewService.create(problemId, new ReviewForm(LocalDate.now(), 5, 10, "Easy recall"));

    var afterReview = dashboardService.buildDashboard();
    assertThat(afterReview.recentReviews()).hasSize(1);
    assertThat(afterReview.stats().totalReviews()).isEqualTo(1);
    assertThat(afterReview.stats().neverReviewedCount()).isZero();
    assertThat(afterReview.topicProgress()).hasSize(1);
  }
}

package com.projects.knowledge_manager.report.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.review.dto.ReviewForm;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.service.TopicService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class WeeklyReportServiceTest {

  @Autowired private WeeklyReportService weeklyReportService;
  @Autowired private ProblemService problemService;
  @Autowired private ReviewService reviewService;
  @Autowired private TopicService topicService;

  private Long problemId;

  @BeforeEach
  void setUp() {
    Long topicId = topicService.create(new TopicForm("Graphs", null, "#22c55e")).id();
    problemId =
        problemService
            .create(
                new ProblemForm(
                    "Course Schedule",
                    "",
                    Difficulty.MEDIUM,
                    "",
                    List.of(topicId),
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
  void buildsCurrentWeekReportWithReview() {
    LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    reviewService.create(problemId, new ReviewForm(monday, 4, 25, "Reviewed DFS topo"));

    var report = weeklyReportService.buildReport(0);

    assertThat(report.currentWeek()).isTrue();
    assertThat(report.weekStart()).isEqualTo(monday);
    assertThat(report.hasActivity()).isTrue();
    assertThat(report.totalSessions()).isEqualTo(1);
    assertThat(report.totalMinutes()).isEqualTo(25);
    assertThat(report.activeDays()).isEqualTo(1);
    assertThat(report.days()).hasSize(7);
    assertThat(report.breakdown()).isNotEmpty();
    assertThat(report.topTopics()).extracting("name").contains("Graphs");
  }

  @Test
  void emptyPastWeekHasNoActivity() {
    var report = weeklyReportService.buildReport(-3);
    assertThat(report.currentWeek()).isFalse();
    assertThat(report.hasActivity()).isFalse();
    assertThat(report.totalSessions()).isZero();
  }
}

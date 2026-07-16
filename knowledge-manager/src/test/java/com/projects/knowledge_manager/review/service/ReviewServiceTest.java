package com.projects.knowledge_manager.review.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.review.dto.ReviewForm;
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
class ReviewServiceTest {

  @Autowired private ReviewService reviewService;
  @Autowired private ProblemService problemService;
  @Autowired private TopicService topicService;

  private Long problemId;

  @BeforeEach
  void setUp() {
    Long topicId = topicService.create(new TopicForm("Graphs", null, "#6366f1")).id();
    problemId =
        problemService
            .create(
                new ProblemForm(
                    "Number of Islands",
                    "",
                    Difficulty.MEDIUM,
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
  void logsReviewAndBuildsHistory() {
    reviewService.create(problemId, new ReviewForm(LocalDate.now(), 4, 20, "Remembered BFS"));

    var status = reviewService.getProblemReviewStatus(problemId);

    assertThat(status.history()).hasSize(1);
    assertThat(status.history().getFirst().rating()).isEqualTo(4);
    assertThat(status.nextReviewDate()).isAfter(LocalDate.now());
  }
}

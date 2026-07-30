package com.projects.knowledge_manager.topic.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.review.dto.ReviewForm;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TopicMarathonServiceTest {

  @Autowired private TopicMarathonService topicMarathonService;
  @Autowired private TopicService topicService;
  @Autowired private ProblemService problemService;
  @Autowired private ReviewService reviewService;

  private Long topicId;
  private Long problemId;

  @BeforeEach
  void setUp() {
    topicId = topicService.create(new TopicForm("Arrays", null, "#ef4444")).id();
    problemId =
        problemService
            .create(
                new ProblemForm(
                    "Two Sum",
                    "",
                    Difficulty.EASY,
                    "desc",
                    List.of(topicId),
                    List.of(),
                    "",
                    false,
                    false,
                    "java",
                    "code",
                    "",
                    "",
                    ""))
            .id();
  }

  @Test
  void marathonQueuesProblemsAndCountsTopicTimeFromNormalReviews() {
    MockHttpSession session = new MockHttpSession();
    var state = topicMarathonService.start(topicId, session);
    assertThat(state.getTopicId()).isEqualTo(topicId);
    assertThat(topicMarathonService.findNextProblemId(topicId, List.of())).contains(problemId);

    reviewService.create(problemId, new ReviewForm(LocalDate.now(), 4, 12, "from queue"));
    assertThat(topicMarathonService.totalReviewMinutesForTopic(topicId)).isEqualTo(12);

    topicMarathonService.recordCompletion(session, problemId, "Two Sum", 4, 12);
    assertThat(topicMarathonService.findActive(session).orElseThrow().completedCount()).isEqualTo(1);
    assertThat(topicMarathonService.findNextProblemId(topicId, List.of(problemId))).isEmpty();

    var ended = topicMarathonService.end(session);
    assertThat(ended.sessionMinutes()).isEqualTo(12);
    assertThat(topicMarathonService.findActive(session)).isEmpty();
  }
}

package com.projects.knowledge_manager.mockinterview.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionForm;
import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import com.projects.knowledge_manager.behavioral.service.BehavioralQuestionService;
import com.projects.knowledge_manager.mockinterview.dto.MockInterviewAnswerForm;
import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.service.TopicService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MockInterviewServiceTest {

  @Autowired private MockInterviewService interviewService;
  @Autowired private TopicService topicService;
  @Autowired private ProblemService problemService;
  @Autowired private BehavioralQuestionService behavioralQuestionService;

  @BeforeEach
  void setUp() {
    Long topicId = topicService.create(new TopicForm("Graphs", null, "#22c55e")).id();
    for (int i = 1; i <= 3; i++) {
      problemService.create(
          new ProblemForm(
              "Algo " + i,
              "",
              Difficulty.MEDIUM,
              "desc " + i,
              List.of(topicId),
              List.of(),
              "",
              false,
              false,
              "java",
              "code",
              "explain",
              "O(n)",
              ""));
    }
    for (int i = 1; i <= 2; i++) {
      behavioralQuestionService.create(
          new BehavioralQuestionForm(
              "Beh " + i,
              BehavioralCategory.LEADERSHIP,
              "Tell me about leadership " + i,
              "S",
              "T",
              "A",
              "R",
              ""));
    }
  }

  @Test
  void startsStandardMixAndCompletesThroughSpacedRepetition() {
    var started = interviewService.start(InterviewFormat.STANDARD_MIX);
    assertThat(started.totalQuestions()).isEqualTo(4);
    assertThat(started.items()).isNotEmpty();

    boolean finished = false;
    int guard = 0;
    while (!finished && guard++ < 10) {
      var current = interviewService.findCurrentItem(started.id()).orElseThrow();
      finished =
          interviewService.completeCurrentItem(
              started.id(), new MockInterviewAnswerForm(90, 4));
      assertThat(current.getQuestionType())
          .isIn(QuestionType.ALGORITHM, QuestionType.BEHAVIORAL, QuestionType.SYSTEM_DESIGN);
    }

    assertThat(finished).isTrue();
    var summary = interviewService.getSummary(started.id());
    assertThat(summary.finishedAt()).isNotNull();
    assertThat(summary.totalDurationSeconds()).isEqualTo(360);
    assertThat(interviewService.buildStats().totalInterviews()).isEqualTo(1);
  }
}

package com.projects.knowledge_manager.mockinterview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.projects.knowledge_manager.behavioral.dto.BehavioralQuestionForm;
import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import com.projects.knowledge_manager.behavioral.service.BehavioralQuestionService;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileForm;
import com.projects.knowledge_manager.interviewprofile.service.InterviewProfileService;
import com.projects.knowledge_manager.mockinterview.dto.MockInterviewAnswerForm;
import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import com.projects.knowledge_manager.problem.dto.ProblemForm;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignProblemForm;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import com.projects.knowledge_manager.systemdesign.service.SystemDesignProblemService;
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
class ProfileDrivenMockInterviewServiceTest {

  @Autowired private MockInterviewService interviewService;
  @Autowired private InterviewProfileService profileService;
  @Autowired private TopicService topicService;
  @Autowired private ProblemService problemService;
  @Autowired private BehavioralQuestionService behavioralQuestionService;
  @Autowired private SystemDesignProblemService systemDesignProblemService;

  private Long profileId;
  private Long profileProblemId;
  private Long outsideProblemId;

  @BeforeEach
  void setUp() {
    Long topicId = topicService.create(new TopicForm("Graphs", null, "#22c55e")).id();
    profileProblemId =
        problemService
            .create(
                new ProblemForm(
                    "Profile Algo",
                    "",
                    Difficulty.HARD,
                    "in profile",
                    topicId,
                    List.of(),
                    "",
                    false,
                    false,
                    "java",
                    "code",
                    "explain",
                    "O(n)",
                    ""))
            .id();
    outsideProblemId =
        problemService
            .create(
                new ProblemForm(
                    "Outside Algo",
                    "",
                    Difficulty.HARD,
                    "outside profile",
                    topicId,
                    List.of(),
                    "",
                    false,
                    false,
                    "java",
                    "code",
                    "explain",
                    "O(n)",
                    ""))
            .id();

    Long behavioralId =
        behavioralQuestionService
            .create(
                new BehavioralQuestionForm(
                    "Profile Beh",
                    BehavioralCategory.LEADERSHIP,
                    "Leadership story",
                    "S",
                    "T",
                    "A",
                    "R",
                    ""))
            .id();

    Long systemDesignId =
        systemDesignProblemService
            .create(
                new SystemDesignProblemForm(
                    "Profile Design",
                    SystemDesignCategory.MESSAGING,
                    Difficulty.HARD,
                    "Design chat",
                    "",
                    45,
                    false,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""))
            .id();

    profileId =
        profileService
            .create(
                new InterviewProfileForm(
                    "Meta E4",
                    "Profile scoped interview",
                    "Meta",
                    "#0668E1",
                    "bi-meta",
                    1,
                    1,
                    1,
                    60,
                    List.of(Difficulty.HARD),
                    List.of(profileProblemId),
                    List.of(behavioralId),
                    List.of(systemDesignId),
                    false,
                    ""))
            .id();
  }

  @Test
  void profileInterviewOnlyUsesAssignedItems() {
    var started = interviewService.startWithProfile(profileId);
    assertThat(started.format()).isEqualTo(InterviewFormat.PROFILE_DRIVEN);
    assertThat(started.profileId()).isEqualTo(profileId);
    assertThat(started.profileName()).isEqualTo("Meta E4");
    assertThat(started.totalQuestions()).isEqualTo(3);

    boolean finished = false;
    int guard = 0;
    while (!finished && guard++ < 10) {
      var current = interviewService.findCurrentItem(started.id()).orElseThrow();
      assertThat(current.getProblemId()).isNotEqualTo(outsideProblemId);
      if (current.getQuestionType() == QuestionType.ALGORITHM) {
        assertThat(current.getProblemId()).isEqualTo(profileProblemId);
      }
      finished =
          interviewService.completeCurrentItem(
              started.id(), new MockInterviewAnswerForm(90, 4));
    }
    assertThat(finished).isTrue();
  }

  @Test
  void emptyProfileAssignmentsYieldEmptyPlan() {
    Long emptyProfileId =
        profileService
            .create(
                new InterviewProfileForm(
                    "Empty Bank",
                    "",
                    "",
                    "#111111",
                    "bi-briefcase",
                    2,
                    2,
                    0,
                    30,
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    false,
                    ""))
            .id();

    assertThatThrownBy(() -> interviewService.startWithProfile(emptyProfileId))
        .isInstanceOf(EmptyInterviewPlanException.class);
  }
}

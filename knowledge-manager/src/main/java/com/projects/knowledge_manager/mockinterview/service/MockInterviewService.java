package com.projects.knowledge_manager.mockinterview.service;

import com.projects.knowledge_manager.behavioral.dto.BehavioralPracticeForm;
import com.projects.knowledge_manager.behavioral.entity.BehavioralQuestion;
import com.projects.knowledge_manager.behavioral.model.PracticeRating;
import com.projects.knowledge_manager.behavioral.repository.BehavioralQuestionRepository;
import com.projects.knowledge_manager.behavioral.service.BehavioralQuestionService;
import com.projects.knowledge_manager.interviewprofile.entity.InterviewProfile;
import com.projects.knowledge_manager.interviewprofile.service.InterviewProfileService;
import com.projects.knowledge_manager.mockinterview.dto.MockInterviewAnswerForm;
import com.projects.knowledge_manager.mockinterview.dto.MockInterviewSessionView;
import com.projects.knowledge_manager.mockinterview.dto.MockInterviewStatsView;
import com.projects.knowledge_manager.mockinterview.dto.MockInterviewSummaryView;
import com.projects.knowledge_manager.mockinterview.entity.MockInterview;
import com.projects.knowledge_manager.mockinterview.entity.MockInterviewItem;
import com.projects.knowledge_manager.mockinterview.generator.InterviewCandidate;
import com.projects.knowledge_manager.mockinterview.generator.InterviewCandidatePool;
import com.projects.knowledge_manager.mockinterview.generator.InterviewGenerator;
import com.projects.knowledge_manager.mockinterview.generator.InterviewPlanBuilder;
import com.projects.knowledge_manager.mockinterview.generator.InterviewPlanItem;
import com.projects.knowledge_manager.mockinterview.generator.ProfileInterviewPatternBuilder;
import com.projects.knowledge_manager.mockinterview.mapper.MockInterviewMapper;
import com.projects.knowledge_manager.mockinterview.model.InterviewFormat;
import com.projects.knowledge_manager.mockinterview.model.QuestionType;
import com.projects.knowledge_manager.mockinterview.repository.MockInterviewRepository;
import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.problem.service.ProblemService;
import com.projects.knowledge_manager.review.dto.ReviewForm;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.systemdesign.dto.SystemDesignReviewForm;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignProblem;
import com.projects.knowledge_manager.systemdesign.repository.SystemDesignProblemRepository;
import com.projects.knowledge_manager.systemdesign.service.SystemDesignProblemService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MockInterviewService {

  private final MockInterviewRepository interviewRepository;
  private final ProblemRepository problemRepository;
  private final BehavioralQuestionRepository behavioralQuestionRepository;
  private final SystemDesignProblemRepository systemDesignProblemRepository;
  private final ReviewService reviewService;
  private final BehavioralQuestionService behavioralQuestionService;
  private final SystemDesignProblemService systemDesignProblemService;
  private final ProblemService problemService;
  private final InterviewProfileService interviewProfileService;
  private final Map<InterviewFormat, InterviewGenerator> generators;

  public MockInterviewService(
      MockInterviewRepository interviewRepository,
      ProblemRepository problemRepository,
      BehavioralQuestionRepository behavioralQuestionRepository,
      SystemDesignProblemRepository systemDesignProblemRepository,
      ReviewService reviewService,
      BehavioralQuestionService behavioralQuestionService,
      SystemDesignProblemService systemDesignProblemService,
      ProblemService problemService,
      InterviewProfileService interviewProfileService,
      Map<InterviewFormat, InterviewGenerator> interviewGenerators) {
    this.interviewRepository = interviewRepository;
    this.problemRepository = problemRepository;
    this.behavioralQuestionRepository = behavioralQuestionRepository;
    this.systemDesignProblemRepository = systemDesignProblemRepository;
    this.reviewService = reviewService;
    this.behavioralQuestionService = behavioralQuestionService;
    this.systemDesignProblemService = systemDesignProblemService;
    this.problemService = problemService;
    this.interviewProfileService = interviewProfileService;
    this.generators = interviewGenerators;
  }

  @Transactional
  public MockInterviewSummaryView start(InterviewFormat format) {
    InterviewGenerator generator = generators.get(format);
    if (generator == null) {
      throw new IllegalArgumentException("Unsupported interview format: " + format);
    }

    List<InterviewPlanItem> plan = generator.generate(buildCandidatePool(null));
    return persistInterview(format, null, null, plan);
  }

  @Transactional
  public MockInterviewSummaryView startWithProfile(Long profileId) {
    InterviewProfile profile = interviewProfileService.getForInterview(profileId);
    List<QuestionType> pattern = ProfileInterviewPatternBuilder.build(profile);
    InterviewCandidatePool pool = buildCandidatePool(profile);
    List<InterviewPlanItem> plan = InterviewPlanBuilder.build(pattern, pool);
    return persistInterview(
        InterviewFormat.PROFILE_DRIVEN, profile.getId(), profile.getName(), plan);
  }

  public Optional<MockInterviewItem> findCurrentItem(Long interviewId) {
    MockInterview interview = getDetailedOrThrow(interviewId);
    return interview.getItems().stream().filter(item -> !item.isCompleted()).findFirst();
  }

  public MockInterviewSessionView buildSession(Long interviewId, boolean revealAnswer) {
    MockInterview interview = getDetailedOrThrow(interviewId);
    MockInterviewItem current =
        interview.getItems().stream()
            .filter(item -> !item.isCompleted())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Interview has no pending questions."));

    long elapsed =
        Math.max(0, Instant.now().getEpochSecond() - interview.getStartedAt().getEpochSecond());

    return new MockInterviewSessionView(
        interview.getId(),
        interview.getFormat().getLabel(),
        current.getItemOrder(),
        interview.getTotalQuestions(),
        elapsed,
        MockInterviewMapper.toItemView(current),
        current.getQuestionType(),
        current.getQuestionType() == QuestionType.ALGORITHM
            ? problemService.findDetailById(current.getProblemId())
            : null,
        current.getQuestionType() == QuestionType.BEHAVIORAL
            ? behavioralQuestionService.findById(current.getBehavioralQuestionId())
            : null,
        current.getQuestionType() == QuestionType.SYSTEM_DESIGN
            ? systemDesignProblemService.findById(current.getSystemDesignProblemId())
            : null,
        revealAnswer);
  }

  @Transactional
  public boolean completeCurrentItem(Long interviewId, MockInterviewAnswerForm form) {
    if (PracticeRating.fromQuality(form.rating()) == null) {
      throw new IllegalArgumentException(
          "Rating must be one of Forgot(1), Hard(3), Good(4), or Easy(5).");
    }

    MockInterview interview = getDetailedOrThrow(interviewId);
    if (interview.isFinished()) {
      throw new IllegalStateException("Interview is already finished.");
    }

    MockInterviewItem current =
        interview.getItems().stream()
            .filter(item -> !item.isCompleted())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Interview has no pending questions."));

    Instant now = Instant.now();
    current.complete(form.durationSeconds(), form.rating(), now);
    recordSpacedRepetitionReview(current, form);

    boolean allDone = interview.getItems().stream().allMatch(MockInterviewItem::isCompleted);
    if (allDone) {
      finishInterview(interview, now);
    }
    return allDone;
  }

  public MockInterviewSummaryView getSummary(Long interviewId) {
    return toSummary(getDetailedOrThrow(interviewId));
  }

  public List<MockInterviewSummaryView> findHistory() {
    return interviewRepository.findAllByOrderByStartedAtDesc().stream()
        .map(
            interview ->
                toSummary(
                    interviewRepository.findDetailedById(interview.getId()).orElse(interview)))
        .toList();
  }

  public MockInterviewStatsView buildStats() {
    long finished = interviewRepository.countByFinishedAtIsNotNull();
    Double avgInterview = interviewRepository.averageFinishedDurationSeconds();
    Double avgQuestion = interviewRepository.averageQuestionDurationSeconds();
    Instant monthStart =
        YearMonth.now().atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);

    return new MockInterviewStatsView(
        finished,
        interviewRepository.sumFinishedDurationSeconds(),
        avgInterview == null ? 0 : avgInterview,
        avgQuestion == null ? 0 : avgQuestion,
        interviewRepository.countFinishedSince(monthStart),
        interviewRepository.maxFinishedDurationSeconds(),
        finished == 0 ? 0 : interviewRepository.minFinishedDurationSeconds(),
        interviewRepository.sumFinishedQuestions());
  }

  private MockInterviewSummaryView persistInterview(
      InterviewFormat format,
      Long profileId,
      String profileName,
      List<InterviewPlanItem> plan) {
    if (plan.isEmpty()) {
      throw new EmptyInterviewPlanException();
    }

    MockInterview interview = new MockInterview(format, Instant.now(), profileId);
    int order = 1;
    for (InterviewPlanItem slot : plan) {
      Long problemId = slot.type() == QuestionType.ALGORITHM ? slot.sourceId() : null;
      Long behavioralId = slot.type() == QuestionType.BEHAVIORAL ? slot.sourceId() : null;
      Long systemDesignId = slot.type() == QuestionType.SYSTEM_DESIGN ? slot.sourceId() : null;
      interview.addItem(
          new MockInterviewItem(
              order++, slot.type(), problemId, behavioralId, systemDesignId, slot.title()));
    }

    return MockInterviewMapper.toSummaryView(
        interviewRepository.save(interview), profileName);
  }

  private MockInterviewSummaryView toSummary(MockInterview interview) {
    String profileName = null;
    if (interview.getProfileId() != null) {
      try {
        profileName = interviewProfileService.findById(interview.getProfileId()).name();
      } catch (RuntimeException ignored) {
        profileName = "Deleted profile";
      }
    }
    return MockInterviewMapper.toSummaryView(interview, profileName);
  }

  private void finishInterview(MockInterview interview, Instant finishedAt) {
    int totalDuration =
        interview.getItems().stream()
            .mapToInt(item -> item.getDurationSeconds() == null ? 0 : item.getDurationSeconds())
            .sum();
    interview.setFinishedAt(finishedAt);
    interview.setTotalDurationSeconds(totalDuration);
  }

  private void recordSpacedRepetitionReview(MockInterviewItem item, MockInterviewAnswerForm form) {
    LocalDate today = LocalDate.now();
    if (item.getQuestionType() == QuestionType.ALGORITHM) {
      int minutes = Math.max(1, (int) Math.ceil(form.durationSeconds() / 60.0));
      reviewService.create(
          item.getProblemId(), new ReviewForm(today, form.rating(), minutes, "Mock interview"));
      return;
    }
    if (item.getQuestionType() == QuestionType.SYSTEM_DESIGN) {
      systemDesignProblemService.recordReview(
          item.getSystemDesignProblemId(),
          new SystemDesignReviewForm(today, form.durationSeconds(), form.rating()));
      return;
    }

    behavioralQuestionService.recordPractice(
        item.getBehavioralQuestionId(),
        new BehavioralPracticeForm(today, form.durationSeconds(), form.rating()));
  }

  /**
   * Builds scored candidates. When {@code profile} is present, only assigned items are eligible
   * and target difficulties are applied to algorithm / system design items.
   */
  InterviewCandidatePool buildCandidatePool(InterviewProfile profile) {
    LocalDate today = LocalDate.now();
    Map<QuestionType, List<InterviewCandidate>> byType = new EnumMap<>(QuestionType.class);
    byType.put(QuestionType.ALGORITHM, new ArrayList<>());
    byType.put(QuestionType.BEHAVIORAL, new ArrayList<>());
    byType.put(QuestionType.SYSTEM_DESIGN, new ArrayList<>());

    Set<Difficulty> targetDifficulties =
        profile == null || profile.getTargetDifficulties().isEmpty()
            ? EnumSet.noneOf(Difficulty.class)
            : EnumSet.copyOf(profile.getTargetDifficulties());

    Iterable<Problem> problems =
        profile == null
            ? problemRepository.findAllByArchivedFalseOrderByTitleAsc()
            : profile.getProblems().stream()
                .filter(problem -> !problem.isArchived())
                .filter(problem -> matchesDifficulty(problem.getDifficulty(), targetDifficulties))
                .toList();

    for (Problem problem : problems) {
      LocalDate next = reviewService.resolveNextReviewDate(problem);
      boolean never = reviewService.isNeverReviewed(problem.getId());
      Integer lastRating = reviewService.findLatestRating(problem.getId()).orElse(null);
      byType
          .get(QuestionType.ALGORITHM)
          .add(
              new InterviewCandidate(
                  QuestionType.ALGORITHM,
                  problem.getId(),
                  problem.getTitle(),
                  next,
                  next.isBefore(today),
                  !next.isAfter(today),
                  never,
                  lastRating,
                  score(next, today, never, lastRating)));
    }

    Iterable<BehavioralQuestion> behavioralQuestions =
        profile == null
            ? behavioralQuestionRepository.findAllByOrderByTitleAsc()
            : profile.getBehavioralQuestions();

    for (BehavioralQuestion question : behavioralQuestions) {
      LocalDate next = behavioralQuestionService.resolveNextReviewDate(question.getId());
      boolean never = behavioralQuestionService.isNeverPracticed(question.getId());
      Integer lastRating =
          behavioralQuestionService.findPracticeHistory(question.getId()).stream()
              .findFirst()
              .map(view -> view.rating())
              .orElse(null);
      byType
          .get(QuestionType.BEHAVIORAL)
          .add(
              new InterviewCandidate(
                  QuestionType.BEHAVIORAL,
                  question.getId(),
                  question.getTitle(),
                  next,
                  next.isBefore(today),
                  !next.isAfter(today),
                  never,
                  lastRating,
                  score(next, today, never, lastRating)));
    }

    Iterable<SystemDesignProblem> systemDesignProblems =
        profile == null
            ? systemDesignProblemRepository.findAllByOrderByTitleAsc()
            : profile.getSystemDesignProblems().stream()
                .filter(problem -> matchesDifficulty(problem.getDifficulty(), targetDifficulties))
                .toList();

    for (SystemDesignProblem problem : systemDesignProblems) {
      LocalDate next = systemDesignProblemService.resolveNextReviewDate(problem.getId());
      boolean never = systemDesignProblemService.isNeverReviewed(problem.getId());
      Integer lastRating =
          systemDesignProblemService.findLatestRating(problem.getId()).orElse(null);
      byType
          .get(QuestionType.SYSTEM_DESIGN)
          .add(
              new InterviewCandidate(
                  QuestionType.SYSTEM_DESIGN,
                  problem.getId(),
                  problem.getTitle(),
                  next,
                  next.isBefore(today),
                  !next.isAfter(today),
                  never,
                  lastRating,
                  score(next, today, never, lastRating)));
    }

    return new InterviewCandidatePool(byType);
  }

  private static boolean matchesDifficulty(
      Difficulty difficulty, Set<Difficulty> targetDifficulties) {
    return targetDifficulties.isEmpty() || targetDifficulties.contains(difficulty);
  }

  static int score(LocalDate nextReviewDate, LocalDate today, boolean neverReviewed, Integer lastRating) {
    int score = 0;
    if (nextReviewDate.isBefore(today)) {
      score += 100;
    } else if (!nextReviewDate.isAfter(today)) {
      score += 80;
    }
    if (neverReviewed) {
      score += 40;
    }
    if (lastRating != null) {
      if (lastRating <= 1) {
        score += 50;
      } else if (lastRating <= 3) {
        score += 30;
      } else if (lastRating == 4) {
        score += 10;
      }
    }
    return score;
  }

  private MockInterview getDetailedOrThrow(Long id) {
    return interviewRepository
        .findDetailedById(id)
        .orElseThrow(() -> new MockInterviewNotFoundException(id));
  }
}

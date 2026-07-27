package com.projects.knowledge_manager.interviewprofile.service;

import com.projects.knowledge_manager.behavioral.entity.BehavioralQuestion;
import com.projects.knowledge_manager.behavioral.repository.BehavioralQuestionRepository;
import com.projects.knowledge_manager.behavioral.service.BehavioralQuestionService;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileForm;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileStatsView;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileSummaryView;
import com.projects.knowledge_manager.interviewprofile.dto.InterviewProfileView;
import com.projects.knowledge_manager.interviewprofile.entity.InterviewProfile;
import com.projects.knowledge_manager.interviewprofile.mapper.InterviewProfileMapper;
import com.projects.knowledge_manager.interviewprofile.repository.InterviewProfileRepository;
import com.projects.knowledge_manager.mockinterview.entity.MockInterview;
import com.projects.knowledge_manager.mockinterview.entity.MockInterviewItem;
import com.projects.knowledge_manager.mockinterview.repository.MockInterviewRepository;
import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignProblem;
import com.projects.knowledge_manager.systemdesign.repository.SystemDesignProblemRepository;
import com.projects.knowledge_manager.systemdesign.service.SystemDesignProblemService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InterviewProfileService {

  private final InterviewProfileRepository profileRepository;
  private final ProblemRepository problemRepository;
  private final BehavioralQuestionRepository behavioralQuestionRepository;
  private final SystemDesignProblemRepository systemDesignProblemRepository;
  private final MockInterviewRepository mockInterviewRepository;
  private final ReviewService reviewService;
  private final BehavioralQuestionService behavioralQuestionService;
  private final SystemDesignProblemService systemDesignProblemService;

  public InterviewProfileService(
      InterviewProfileRepository profileRepository,
      ProblemRepository problemRepository,
      BehavioralQuestionRepository behavioralQuestionRepository,
      SystemDesignProblemRepository systemDesignProblemRepository,
      MockInterviewRepository mockInterviewRepository,
      ReviewService reviewService,
      BehavioralQuestionService behavioralQuestionService,
      SystemDesignProblemService systemDesignProblemService) {
    this.profileRepository = profileRepository;
    this.problemRepository = problemRepository;
    this.behavioralQuestionRepository = behavioralQuestionRepository;
    this.systemDesignProblemRepository = systemDesignProblemRepository;
    this.mockInterviewRepository = mockInterviewRepository;
    this.reviewService = reviewService;
    this.behavioralQuestionService = behavioralQuestionService;
    this.systemDesignProblemService = systemDesignProblemService;
  }

  public List<InterviewProfileSummaryView> findAll(boolean includeArchived) {
    List<InterviewProfile> profiles =
        includeArchived
            ? profileRepository.findAllByOrderByNameAsc()
            : profileRepository.findAllByArchivedFalseOrderByNameAsc();
    return profiles.stream().map(this::toSummaryHydrated).toList();
  }

  public InterviewProfileView findById(Long id) {
    return InterviewProfileMapper.toView(loadFully(id));
  }

  public InterviewProfileForm findFormById(Long id) {
    return InterviewProfileMapper.toForm(loadFully(id));
  }

  /** Fully loaded profile for interview generation (assignments + settings). */
  public InterviewProfile getForInterview(Long id) {
    InterviewProfile profile = loadFully(id);
    if (profile.isArchived()) {
      throw new IllegalArgumentException("Cannot start an interview with an archived profile.");
    }
    return profile;
  }

  public List<InterviewProfileStatsView> buildStatsByProfile() {
    LocalDate today = LocalDate.now();
    return profileRepository.findAllByArchivedFalseOrderByNameAsc().stream()
        .map(profile -> buildStats(loadFully(profile.getId()), today))
        .toList();
  }

  @Transactional
  public InterviewProfileView create(InterviewProfileForm form) {
    validateForm(form, null);
    InterviewProfile saved =
        profileRepository.save(
            InterviewProfileMapper.toEntity(
                form,
                resolveProblems(form.problemIds()),
                resolveBehavioral(form.behavioralQuestionIds()),
                resolveSystemDesign(form.systemDesignProblemIds())));
    return InterviewProfileMapper.toView(loadFully(saved.getId()));
  }

  @Transactional
  public InterviewProfileView update(Long id, InterviewProfileForm form) {
    validateForm(form, id);
    InterviewProfile profile = loadFully(id);
    InterviewProfileMapper.updateEntity(
        profile,
        form,
        resolveProblems(form.problemIds()),
        resolveBehavioral(form.behavioralQuestionIds()),
        resolveSystemDesign(form.systemDesignProblemIds()));
    return InterviewProfileMapper.toView(profile);
  }

  @Transactional
  public void delete(Long id) {
    profileRepository.delete(getOrThrow(id));
  }

  @Transactional
  public InterviewProfileView duplicate(Long id) {
    InterviewProfile source = loadFully(id);
    String uniqueName = uniqueCopyName(source.getName() + " (Copy)");

    InterviewProfileForm form =
        new InterviewProfileForm(
            uniqueName,
            source.getDescription(),
            source.getCompany(),
            source.getColor(),
            source.getIcon(),
            source.getBehavioralQuestionCount(),
            source.getAlgorithmQuestionCount(),
            source.getSystemDesignQuestionCount(),
            source.getMaxDurationMinutes(),
            List.copyOf(source.getTargetDifficulties()),
            source.getProblems().stream().map(Problem::getId).toList(),
            source.getBehavioralQuestions().stream().map(BehavioralQuestion::getId).toList(),
            source.getSystemDesignProblems().stream().map(SystemDesignProblem::getId).toList(),
            false,
            source.getExtraSettingsJson());

    return create(form);
  }

  @Transactional
  public void setArchived(Long id, boolean archived) {
    getOrThrow(id).setArchived(archived);
  }

  private InterviewProfileStatsView buildStats(InterviewProfile profile, LocalDate today) {
    List<MockInterview> interviews =
        mockInterviewRepository.findDetailedByProfileIdOrderByStartedAtDesc(profile.getId());

    List<MockInterview> finished =
        interviews.stream().filter(MockInterview::isFinished).toList();

    long questionsReviewed =
        finished.stream()
            .flatMap(interview -> interview.getItems().stream())
            .filter(MockInterviewItem::isCompleted)
            .count();

    double averageDuration =
        finished.stream().mapToInt(MockInterview::getTotalDurationSeconds).average().orElse(0);

    double averageRating =
        finished.stream()
            .flatMap(interview -> interview.getItems().stream())
            .filter(item -> item.getRating() != null)
            .mapToInt(MockInterviewItem::getRating)
            .average()
            .orElse(0);

    long dueReviews = countDueAssignedItems(profile, today);
    int assignedTotal =
        profile.getProblems().size()
            + profile.getBehavioralQuestions().size()
            + profile.getSystemDesignProblems().size();
    long mastered = countMasteredAssignedItems(profile, today);
    double completionPercentage =
        assignedTotal == 0 ? 0 : Math.round((mastered * 1000.0 / assignedTotal)) / 10.0;
    double masteryScore = Math.round(averageRating * 20.0) / 10.0;

    Instant lastInterviewAt =
        interviews.stream().map(MockInterview::getStartedAt).findFirst().orElse(null);

    return new InterviewProfileStatsView(
        profile.getId(),
        profile.getName(),
        profile.getCompany(),
        profile.getColor(),
        profile.getIcon(),
        finished.size(),
        questionsReviewed,
        Math.round(averageDuration * 10.0) / 10.0,
        Math.round(averageRating * 10.0) / 10.0,
        masteryScore,
        dueReviews,
        completionPercentage,
        lastInterviewAt,
        lastInterviewAt == null ? null : lastInterviewAt.atZone(ZoneOffset.UTC).toLocalDate());
  }

  private long countDueAssignedItems(InterviewProfile profile, LocalDate today) {
    long due = 0;
    for (Problem problem : profile.getProblems()) {
      if (!problem.isArchived()
          && !reviewService.resolveNextReviewDate(problem).isAfter(today)) {
        due++;
      }
    }
    for (BehavioralQuestion question : profile.getBehavioralQuestions()) {
      if (!behavioralQuestionService.resolveNextReviewDate(question.getId()).isAfter(today)) {
        due++;
      }
    }
    for (SystemDesignProblem problem : profile.getSystemDesignProblems()) {
      if (!systemDesignProblemService.resolveNextReviewDate(problem.getId()).isAfter(today)) {
        due++;
      }
    }
    return due;
  }

  private long countMasteredAssignedItems(InterviewProfile profile, LocalDate today) {
    long mastered = 0;
    for (Problem problem : profile.getProblems()) {
      if (problem.isArchived()) {
        continue;
      }
      var rating = reviewService.findLatestRating(problem.getId());
      if (rating.isPresent()
          && rating.get() >= 5
          && reviewService.resolveNextReviewDate(problem).isAfter(today.plusDays(14))) {
        mastered++;
      }
    }
    for (BehavioralQuestion question : profile.getBehavioralQuestions()) {
      var history = behavioralQuestionService.findPracticeHistory(question.getId());
      if (!history.isEmpty()
          && history.getFirst().rating() >= 5
          && history.getFirst().nextReviewDate().isAfter(today.plusDays(14))) {
        mastered++;
      }
    }
    for (SystemDesignProblem problem : profile.getSystemDesignProblems()) {
      var rating = systemDesignProblemService.findLatestRating(problem.getId());
      if (rating.isPresent()
          && rating.get() >= 5
          && systemDesignProblemService
              .resolveNextReviewDate(problem.getId())
              .isAfter(today.plusDays(14))) {
        mastered++;
      }
    }
    return mastered;
  }

  private InterviewProfileSummaryView toSummaryHydrated(InterviewProfile thin) {
    return InterviewProfileMapper.toSummary(loadFully(thin.getId()));
  }

  private InterviewProfile loadFully(Long id) {
    InterviewProfile profile = getOrThrow(id);
    profile.getTargetDifficulties().size();
    profile.getProblems().size();
    profile.getBehavioralQuestions().size();
    profile.getSystemDesignProblems().size();
    return profile;
  }

  private InterviewProfile getOrThrow(Long id) {
    return profileRepository
        .findById(id)
        .orElseThrow(() -> new InterviewProfileNotFoundException(id));
  }

  private void validateForm(InterviewProfileForm form, Long currentId) {
    if (form.behavioralQuestionCount()
            + form.algorithmQuestionCount()
            + form.systemDesignQuestionCount()
        <= 0) {
      throw new IllegalArgumentException(
          "Profile must configure at least one algorithm, behavioral, or system design question.");
    }

    boolean duplicate =
        currentId == null
            ? profileRepository.existsByNameIgnoreCase(form.name().trim())
            : profileRepository.existsByNameIgnoreCaseAndIdNot(form.name().trim(), currentId);
    if (duplicate) {
      throw new DuplicateInterviewProfileNameException(form.name().trim());
    }

    if (form.extraSettingsJson() != null
        && !form.extraSettingsJson().isBlank()
        && !isValidJsonObjectOrEmpty(form.extraSettingsJson().trim())) {
      throw new IllegalArgumentException("Extra settings must be valid JSON or blank.");
    }
  }

  private boolean isValidJsonObjectOrEmpty(String value) {
    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      return true;
    }
    return (trimmed.startsWith("{") && trimmed.endsWith("}"))
        || (trimmed.startsWith("[") && trimmed.endsWith("]"));
  }

  private String uniqueCopyName(String baseName) {
    String candidate = baseName;
    int suffix = 2;
    while (profileRepository.existsByNameIgnoreCase(candidate)) {
      candidate = baseName + " " + suffix++;
    }
    if (candidate.length() > 100) {
      candidate = candidate.substring(0, 100);
    }
    return candidate;
  }

  private Set<Problem> resolveProblems(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Set.of();
    }
    List<Problem> found = problemRepository.findAllById(ids);
    if (found.size() != new HashSet<>(ids).size()) {
      throw new IllegalArgumentException("One or more algorithm problems were not found.");
    }
    return new HashSet<>(found);
  }

  private Set<BehavioralQuestion> resolveBehavioral(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Set.of();
    }
    List<BehavioralQuestion> found = behavioralQuestionRepository.findAllById(ids);
    if (found.size() != new HashSet<>(ids).size()) {
      throw new IllegalArgumentException("One or more behavioral questions were not found.");
    }
    return new HashSet<>(found);
  }

  private Set<SystemDesignProblem> resolveSystemDesign(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Set.of();
    }
    List<SystemDesignProblem> found = systemDesignProblemRepository.findAllById(ids);
    if (found.size() != new HashSet<>(ids).size()) {
      throw new IllegalArgumentException("One or more system design problems were not found.");
    }
    return new HashSet<>(found);
  }
}

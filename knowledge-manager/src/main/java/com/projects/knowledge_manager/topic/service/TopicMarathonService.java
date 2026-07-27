package com.projects.knowledge_manager.topic.service;

import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.review.repository.ReviewRepository;
import com.projects.knowledge_manager.review.service.ReviewService;
import com.projects.knowledge_manager.topic.dto.TopicMarathonCompletedItem;
import com.projects.knowledge_manager.topic.dto.TopicMarathonState;
import com.projects.knowledge_manager.topic.entity.Topic;
import com.projects.knowledge_manager.topic.repository.TopicRepository;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TopicMarathonService {

  public static final String SESSION_KEY = "topicMarathon";

  private final TopicRepository topicRepository;
  private final ProblemRepository problemRepository;
  private final ReviewRepository reviewRepository;
  private final ReviewService reviewService;

  public TopicMarathonService(
      TopicRepository topicRepository,
      ProblemRepository problemRepository,
      ReviewRepository reviewRepository,
      ReviewService reviewService) {
    this.topicRepository = topicRepository;
    this.problemRepository = problemRepository;
    this.reviewRepository = reviewRepository;
    this.reviewService = reviewService;
  }

  public TopicMarathonState start(Long topicId, HttpSession session) {
    Topic topic = getTopicOrThrow(topicId);
    List<Problem> problems = problemRepository.findAllByTopicIdAndArchivedFalseOrderByTitleAsc(topicId);
    if (problems.isEmpty()) {
      throw new EmptyTopicMarathonException(topic.getName());
    }

    TopicMarathonState state =
        new TopicMarathonState(
            topic.getId(), topic.getName(), topic.getColor(), System.currentTimeMillis());
    session.setAttribute(SESSION_KEY, state);
    return state;
  }

  public Optional<TopicMarathonState> findActive(HttpSession session) {
    Object value = session.getAttribute(SESSION_KEY);
    if (value instanceof TopicMarathonState state) {
      return Optional.of(state);
    }
    return Optional.empty();
  }

  public Optional<TopicMarathonState> findActiveForTopic(HttpSession session, Long topicId) {
    return findActive(session).filter(state -> state.getTopicId().equals(topicId));
  }

  public Optional<Long> findNextProblemId(Long topicId, List<Long> excludeIds) {
    Set<Long> excluded = new HashSet<>(excludeIds == null ? List.of() : excludeIds);
    LocalDate today = LocalDate.now();

    record Candidate(Long id, boolean overdue, boolean due, LocalDate nextReviewDate, String title) {}

    return problemRepository.findAllByTopicIdAndArchivedFalseOrderByTitleAsc(topicId).stream()
        .filter(problem -> !excluded.contains(problem.getId()))
        .map(
            problem -> {
              LocalDate nextReviewDate = reviewService.resolveNextReviewDate(problem);
              return new Candidate(
                  problem.getId(),
                  nextReviewDate.isBefore(today),
                  !nextReviewDate.isAfter(today),
                  nextReviewDate,
                  problem.getTitle());
            })
        .sorted(
            Comparator.comparing(Candidate::due)
                .reversed()
                .thenComparing(Candidate::overdue)
                .reversed()
                .thenComparing(Candidate::nextReviewDate)
                .thenComparing(Candidate::title, String.CASE_INSENSITIVE_ORDER))
        .map(Candidate::id)
        .findFirst();
  }

  public void recordCompletion(
      HttpSession session, Long problemId, String title, int rating, int durationMinutes) {
    TopicMarathonState state =
        findActive(session)
            .orElseThrow(() -> new IllegalStateException("No active topic marathon."));
    state
        .getCompleted()
        .add(new TopicMarathonCompletedItem(problemId, title, rating, durationMinutes));
    session.setAttribute(SESSION_KEY, state);
  }

  public TopicMarathonState end(HttpSession session) {
    TopicMarathonState state =
        findActive(session)
            .orElseThrow(() -> new IllegalStateException("No active topic marathon."));
    session.removeAttribute(SESSION_KEY);
    return state;
  }

  public void clear(HttpSession session) {
    session.removeAttribute(SESSION_KEY);
  }

  public long totalReviewMinutesForTopic(Long topicId) {
    return reviewRepository.sumDurationMinutesByTopicId(topicId);
  }

  public Map<Long, Long> totalReviewMinutesByTopic() {
    Map<Long, Long> minutes = new HashMap<>();
    for (Object[] row : reviewRepository.sumDurationMinutesGroupedByTopic()) {
      minutes.put((Long) row[0], ((Number) row[1]).longValue());
    }
    return minutes;
  }

  private Topic getTopicOrThrow(Long topicId) {
    return topicRepository
        .findById(topicId)
        .orElseThrow(() -> new TopicNotFoundException(topicId));
  }
}

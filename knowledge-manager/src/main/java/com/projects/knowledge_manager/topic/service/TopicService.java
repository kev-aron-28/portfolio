package com.projects.knowledge_manager.topic.service;

import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.repository.ProblemRepository;
import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.dto.TopicView;
import com.projects.knowledge_manager.topic.entity.Topic;
import com.projects.knowledge_manager.topic.mapper.TopicMapper;
import com.projects.knowledge_manager.topic.repository.TopicRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TopicService {

  private final TopicRepository topicRepository;
  private final ProblemRepository problemRepository;

  public TopicService(TopicRepository topicRepository, ProblemRepository problemRepository) {
    this.topicRepository = topicRepository;
    this.problemRepository = problemRepository;
  }

  public List<TopicView> findAll() {
    return topicRepository.findAllByOrderByNameAsc().stream()
        .map(TopicMapper::toView)
        .toList();
  }

  public TopicView findById(Long id) {
    return TopicMapper.toView(getTopicOrThrow(id));
  }

  public TopicForm findFormById(Long id) {
    List<Long> problemIds =
        problemRepository.findAllByTopicIdAndArchivedFalseOrderByTitleAsc(id).stream()
            .map(Problem::getId)
            .toList();
    return TopicMapper.toForm(getTopicOrThrow(id), problemIds);
  }

  @Transactional
  public TopicView create(TopicForm form) {
    validateUniqueName(form.name(), null);
    Topic saved = topicRepository.save(TopicMapper.toEntity(form));
    syncProblemMembership(saved, form.problemIds());
    return TopicMapper.toView(saved);
  }

  @Transactional
  public TopicView update(Long id, TopicForm form) {
    Topic topic = getTopicOrThrow(id);
    validateUniqueName(form.name(), id);
    TopicMapper.updateEntity(topic, form);
    syncProblemMembership(topic, form.problemIds());
    return TopicMapper.toView(topic);
  }

  @Transactional
  public void delete(Long id) {
    Topic topic = getTopicOrThrow(id);
    long problemCount =
        problemRepository.findAllByTopicIdAndArchivedFalseOrderByTitleAsc(id).size();
    if (problemCount > 0) {
      throw new IllegalArgumentException(
          "Cannot delete a topic that still has problems. Remove them from this topic first.");
    }
    topicRepository.delete(topic);
  }

  /**
   * Syncs which problems belong to this topic. Selected problems are linked; deselected ones are
   * unlinked. A problem must keep at least one topic.
   */
  private void syncProblemMembership(Topic topic, List<Long> problemIds) {
    Set<Long> selectedIds =
        problemIds == null ? Set.of() : problemIds.stream().collect(Collectors.toSet());

    List<Problem> currentlyLinked =
        problemRepository.findAllByTopicIdAndArchivedFalseOrderByTitleAsc(topic.getId());
    Set<Long> currentlyLinkedIds =
        currentlyLinked.stream().map(Problem::getId).collect(Collectors.toSet());

    Set<Long> toAdd = new HashSet<>(selectedIds);
    toAdd.removeAll(currentlyLinkedIds);

    Set<Long> toRemove = new HashSet<>(currentlyLinkedIds);
    toRemove.removeAll(selectedIds);

    if (!toAdd.isEmpty()) {
      List<Problem> problems = problemRepository.findAllById(toAdd);
      if (problems.size() != toAdd.size()) {
        throw new IllegalArgumentException("One or more problems were not found.");
      }
      for (Problem problem : problems) {
        problem.getTopics().add(topic);
      }
    }

    for (Problem problem : currentlyLinked) {
      if (!toRemove.contains(problem.getId())) {
        continue;
      }
      if (problem.getTopics().size() <= 1) {
        throw new IllegalArgumentException(
            "Cannot remove \""
                + problem.getTitle()
                + "\" from this topic — it must belong to at least one topic.");
      }
      problem.getTopics().removeIf(existing -> existing.getId().equals(topic.getId()));
    }
  }

  private Topic getTopicOrThrow(Long id) {
    return topicRepository
        .findById(id)
        .orElseThrow(() -> new TopicNotFoundException(id));
  }

  private void validateUniqueName(String name, Long excludeId) {
    boolean exists =
        excludeId == null
            ? topicRepository.existsByNameIgnoreCase(name.trim())
            : topicRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), excludeId);

    if (exists) {
      throw new DuplicateTopicNameException(name.trim());
    }
  }
}

package com.projects.knowledge_manager.topic.service;

import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.dto.TopicView;
import com.projects.knowledge_manager.topic.entity.Topic;
import com.projects.knowledge_manager.topic.mapper.TopicMapper;
import com.projects.knowledge_manager.topic.repository.TopicRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TopicService {

  private final TopicRepository topicRepository;

  public TopicService(TopicRepository topicRepository) {
    this.topicRepository = topicRepository;
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
    return TopicMapper.toForm(getTopicOrThrow(id));
  }

  @Transactional
  public TopicView create(TopicForm form) {
    validateUniqueName(form.name(), null);
    Topic saved = topicRepository.save(TopicMapper.toEntity(form));
    return TopicMapper.toView(saved);
  }

  @Transactional
  public TopicView update(Long id, TopicForm form) {
    Topic topic = getTopicOrThrow(id);
    validateUniqueName(form.name(), id);
    TopicMapper.updateEntity(topic, form);
    return TopicMapper.toView(topic);
  }

  @Transactional
  public void delete(Long id) {
    Topic topic = getTopicOrThrow(id);
    topicRepository.delete(topic);
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

package com.projects.knowledge_manager.tag.service;

import com.projects.knowledge_manager.problem.service.InvalidTagSelectionException;
import com.projects.knowledge_manager.tag.dto.TagForm;
import com.projects.knowledge_manager.tag.dto.TagView;
import com.projects.knowledge_manager.tag.entity.Tag;
import com.projects.knowledge_manager.tag.mapper.TagMapper;
import com.projects.knowledge_manager.tag.repository.TagRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TagService {

  private static final int MAX_TAG_NAME_LENGTH = 100;

  private final TagRepository tagRepository;

  public TagService(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  public List<TagView> findAll() {
    return tagRepository.findAllByOrderByNameAsc().stream().map(TagMapper::toView).toList();
  }

  public TagForm findFormById(Long id) {
    return TagMapper.toForm(getTagOrThrow(id));
  }

  @Transactional
  public TagView create(TagForm form) {
    validateUniqueName(form.name(), null);
    Tag saved = tagRepository.save(TagMapper.toEntity(form));
    return TagMapper.toView(saved);
  }

  @Transactional
  public TagView update(Long id, TagForm form) {
    Tag tag = getTagOrThrow(id);
    validateUniqueName(form.name(), id);
    TagMapper.updateEntity(tag, form);
    return TagMapper.toView(tag);
  }

  @Transactional
  public void delete(Long id) {
    tagRepository.delete(getTagOrThrow(id));
  }

  @Transactional
  public Set<Tag> resolveTagsForAssignment(List<Long> tagIds, String newTagNames) {
    Set<Tag> tags = new HashSet<>(resolveExistingTags(tagIds));
    tags.addAll(findOrCreateByNames(parseTagNames(newTagNames)));
    return tags;
  }

  private List<Tag> resolveExistingTags(List<Long> tagIds) {
    if (tagIds == null || tagIds.isEmpty()) {
      return List.of();
    }
    List<Tag> tags = tagRepository.findAllByIdIn(tagIds);
    if (tags.size() != tagIds.size()) {
      throw new InvalidTagSelectionException();
    }
    return tags;
  }

  private List<Tag> findOrCreateByNames(List<String> names) {
    List<Tag> createdOrFound = new ArrayList<>();
    for (String name : names) {
      if (name.length() > MAX_TAG_NAME_LENGTH) {
        throw new InvalidTagNameException(name);
      }
      Tag tag =
          tagRepository
              .findByNameIgnoreCase(name)
              .orElseGet(() -> tagRepository.save(new Tag(name)));
      createdOrFound.add(tag);
    }
    return createdOrFound;
  }

  static List<String> parseTagNames(String raw) {
    if (raw == null || raw.isBlank()) {
      return List.of();
    }

    Set<String> seen = new HashSet<>();
    List<String> names = new ArrayList<>();
    for (String part : raw.split("[,\\n]+")) {
      String trimmed = part.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      String key = trimmed.toLowerCase();
      if (seen.add(key)) {
        names.add(trimmed);
      }
    }
    return names;
  }

  private Tag getTagOrThrow(Long id) {
    return tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
  }

  private void validateUniqueName(String name, Long excludeId) {
    boolean exists =
        excludeId == null
            ? tagRepository.existsByNameIgnoreCase(name.trim())
            : tagRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), excludeId);

    if (exists) {
      throw new DuplicateTagNameException(name.trim());
    }
  }
}

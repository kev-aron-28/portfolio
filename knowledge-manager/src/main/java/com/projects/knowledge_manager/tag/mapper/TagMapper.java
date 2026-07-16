package com.projects.knowledge_manager.tag.mapper;

import com.projects.knowledge_manager.tag.dto.TagForm;
import com.projects.knowledge_manager.tag.dto.TagView;
import com.projects.knowledge_manager.tag.entity.Tag;

public final class TagMapper {

  private TagMapper() {}

  public static TagView toView(Tag tag) {
    return new TagView(tag.getId(), tag.getName(), tag.getCreatedAt());
  }

  public static TagForm toForm(Tag tag) {
    return new TagForm(tag.getName());
  }

  public static Tag toEntity(TagForm form) {
    return new Tag(form.name().trim());
  }

  public static void updateEntity(Tag tag, TagForm form) {
    tag.setName(form.name().trim());
  }
}

package com.projects.knowledge_manager.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.projects.knowledge_manager.tag.dto.TagForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TagServiceTest {

  @Autowired private TagService tagService;

  @Test
  void createsTag() {
    var created = tagService.create(new TagForm("BFS"));
    assertThat(created.name()).isEqualTo("BFS");
    assertThat(tagService.findAll()).hasSize(1);
  }

  @Test
  void rejectsDuplicateTagNames() {
    tagService.create(new TagForm("DFS"));
    assertThatThrownBy(() -> tagService.create(new TagForm("dfs")))
        .isInstanceOf(DuplicateTagNameException.class);
  }
}

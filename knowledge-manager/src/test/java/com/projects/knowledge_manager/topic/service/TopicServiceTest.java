package com.projects.knowledge_manager.topic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.projects.knowledge_manager.topic.dto.TopicForm;
import com.projects.knowledge_manager.topic.dto.TopicView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TopicServiceTest {

  @Autowired private TopicService topicService;

  @Test
  void createsAndFindsTopic() {
    TopicView created =
        topicService.create(new TopicForm("Arrays", "Array problems", "#ef4444"));

    assertThat(created.name()).isEqualTo("Arrays");
    assertThat(created.description()).isEqualTo("Array problems");
    assertThat(created.color()).isEqualTo("#ef4444");
    assertThat(topicService.findAll()).hasSize(1);
  }

  @Test
  void rejectsDuplicateNames() {
    topicService.create(new TopicForm("Trees", null, "#22c55e"));

    assertThatThrownBy(() -> topicService.create(new TopicForm("trees", null, "#22c55e")))
        .isInstanceOf(DuplicateTopicNameException.class);
  }
}

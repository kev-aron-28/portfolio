package com.projects.knowledge_manager.topic.repository;

import com.projects.knowledge_manager.topic.entity.Topic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {

  List<Topic> findAllByOrderByNameAsc();

  boolean existsByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}

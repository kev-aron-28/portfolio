package com.projects.knowledge_manager.tag.repository;

import com.projects.knowledge_manager.tag.entity.Tag;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

  List<Tag> findAllByOrderByNameAsc();

  Optional<Tag> findByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

  List<Tag> findAllByIdIn(Collection<Long> ids);
}

package com.projects.knowledge_manager.problem.repository;

import com.projects.knowledge_manager.problem.entity.Problem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository extends JpaRepository<Problem, Long>, JpaSpecificationExecutor<Problem> {

  @EntityGraph(attributePaths = {"topics", "tags"})
  List<Problem> findAllByArchivedOrderByUpdatedAtDesc(boolean archived);

  @EntityGraph(attributePaths = {"topics"})
  List<Problem> findAllByArchivedFalseOrderByTitleAsc();

  @EntityGraph(attributePaths = {"topics"})
  @Query(
      """
      SELECT DISTINCT p FROM Problem p
      JOIN p.topics t
      WHERE t.id = :topicId AND p.archived = false
      ORDER BY p.title ASC
      """)
  List<Problem> findAllByTopicIdAndArchivedFalseOrderByTitleAsc(@Param("topicId") Long topicId);

  long countByArchivedFalse();

  @EntityGraph(attributePaths = {"topics", "tags", "solution"})
  @Query("SELECT p FROM Problem p WHERE p.id = :id")
  Optional<Problem> findDetailedById(@Param("id") Long id);
}

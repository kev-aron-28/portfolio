package com.projects.knowledge_manager.systemdesign.repository;

import com.projects.knowledge_manager.problem.model.Difficulty;
import com.projects.knowledge_manager.systemdesign.entity.SystemDesignProblem;
import com.projects.knowledge_manager.systemdesign.model.SystemDesignCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SystemDesignProblemRepository extends JpaRepository<SystemDesignProblem, Long> {

  List<SystemDesignProblem> findAllByOrderByTitleAsc();

  @Query(
      """
      SELECT DISTINCT p FROM SystemDesignProblem p
      LEFT JOIN FETCH p.document
      LEFT JOIN FETCH p.tags
      WHERE p.id = :id
      """)
  Optional<SystemDesignProblem> findDetailedById(@Param("id") Long id);

  @Query(
      """
      SELECT DISTINCT p FROM SystemDesignProblem p
      LEFT JOIN FETCH p.tags
      ORDER BY p.title ASC
      """)
  List<SystemDesignProblem> findAllWithTags();

  @Query(
      """
      SELECT DISTINCT p FROM SystemDesignProblem p
      LEFT JOIN FETCH p.tags
      WHERE (:category IS NULL OR p.category = :category)
        AND (:difficulty IS NULL OR p.difficulty = :difficulty)
        AND (:favoriteOnly = FALSE OR p.favorite = TRUE)
        AND (
          :query = ''
          OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
          OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
        )
      ORDER BY p.title ASC
      """)
  List<SystemDesignProblem> search(
      @Param("category") SystemDesignCategory category,
      @Param("difficulty") Difficulty difficulty,
      @Param("favoriteOnly") boolean favoriteOnly,
      @Param("query") String query);
}

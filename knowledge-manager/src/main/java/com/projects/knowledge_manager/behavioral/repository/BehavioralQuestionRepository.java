package com.projects.knowledge_manager.behavioral.repository;

import com.projects.knowledge_manager.behavioral.entity.BehavioralQuestion;
import com.projects.knowledge_manager.behavioral.model.BehavioralCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BehavioralQuestionRepository extends JpaRepository<BehavioralQuestion, Long> {

  List<BehavioralQuestion> findAllByOrderByTitleAsc();

  List<BehavioralQuestion> findByCategoryOrderByTitleAsc(BehavioralCategory category);

  @Query(
      """
      SELECT q FROM BehavioralQuestion q
      WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :query, '%'))
         OR LOWER(q.title) LIKE LOWER(CONCAT('%', :query, '%'))
      ORDER BY q.title ASC
      """)
  List<BehavioralQuestion> searchByTitleOrQuestion(@Param("query") String query);

  @Query(
      """
      SELECT q FROM BehavioralQuestion q
      WHERE q.category = :category
        AND (LOWER(q.question) LIKE LOWER(CONCAT('%', :query, '%'))
          OR LOWER(q.title) LIKE LOWER(CONCAT('%', :query, '%')))
      ORDER BY q.title ASC
      """)
  List<BehavioralQuestion> searchByCategoryAndText(
      @Param("category") BehavioralCategory category, @Param("query") String query);
}

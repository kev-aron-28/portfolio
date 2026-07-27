package com.projects.knowledge_manager.systemdesign.repository;

import com.projects.knowledge_manager.systemdesign.entity.SystemDesignReview;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SystemDesignReviewRepository extends JpaRepository<SystemDesignReview, Long> {

  @Query(
      """
      SELECT r FROM SystemDesignReview r
      JOIN FETCH r.problem p
      WHERE p.id = :problemId
      ORDER BY r.reviewDate DESC, r.id DESC
      """)
  List<SystemDesignReview> findByProblemIdOrderByReviewDateDescIdDesc(
      @Param("problemId") Long problemId);

  long countByProblemId(Long problemId);

  @Query(
      """
      SELECT AVG(r.durationSeconds)
      FROM SystemDesignReview r
      """)
  Double averageDurationSeconds();

  @Query(
      """
      SELECT AVG(r.rating)
      FROM SystemDesignReview r
      """)
  Double averageRating();

  @Query(
      """
      SELECT p.category, COUNT(r)
      FROM SystemDesignReview r
      JOIN r.problem p
      GROUP BY p.category
      ORDER BY COUNT(r) DESC
      """)
  List<Object[]> countGroupedByCategory();

  @Query(
      """
      SELECT r.reviewDate, COUNT(r)
      FROM SystemDesignReview r
      WHERE r.reviewDate >= :fromDate
      GROUP BY r.reviewDate
      ORDER BY r.reviewDate ASC
      """)
  List<Object[]> countReviewsGroupedByDate(@Param("fromDate") LocalDate fromDate);

  @Query(
      """
      SELECT MAX(r.reviewDate)
      FROM SystemDesignReview r
      """)
  Optional<LocalDate> findLastReviewDate();
}

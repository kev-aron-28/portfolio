package com.projects.knowledge_manager.review.repository;

import com.projects.knowledge_manager.review.entity.Review;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query(
      """
      SELECT r FROM Review r
      JOIN FETCH r.problem p
      JOIN FETCH p.topic
      WHERE p.id = :problemId
      ORDER BY r.reviewDate DESC, r.id DESC
      """)
  List<Review> findByProblemIdOrderByReviewDateDescIdDesc(@Param("problemId") Long problemId);

  @Query(
      """
      SELECT r FROM Review r
      JOIN FETCH r.problem p
      JOIN FETCH p.topic
      ORDER BY r.reviewDate DESC, r.id DESC
      """)
  List<Review> findRecentReviews(Pageable pageable);

  @Query(
      """
      SELECT r.reviewDate, COUNT(r)
      FROM Review r
      WHERE r.reviewDate >= :startDate
      GROUP BY r.reviewDate
      ORDER BY r.reviewDate
      """)
  List<Object[]> countReviewsGroupedByDate(@Param("startDate") LocalDate startDate);

  long countByReviewDateBetween(LocalDate startDate, LocalDate endDate);

  @Query(
      """
      SELECT r FROM Review r
      JOIN FETCH r.problem p
      JOIN FETCH p.topic
      WHERE p.archived = false
      ORDER BY r.reviewDate DESC, r.id DESC
      """)
  List<Review> findAllWithActiveProblemsOrderByReviewDateDesc();

  @Query(
      """
      SELECT COALESCE(SUM(r.reviewDuration), 0)
      FROM Review r
      JOIN r.problem p
      WHERE p.topic.id = :topicId
        AND p.archived = false
      """)
  long sumDurationMinutesByTopicId(@Param("topicId") Long topicId);

  @Query(
      """
      SELECT p.topic.id, COALESCE(SUM(r.reviewDuration), 0)
      FROM Review r
      JOIN r.problem p
      WHERE p.archived = false
      GROUP BY p.topic.id
      """)
  List<Object[]> sumDurationMinutesGroupedByTopic();
}

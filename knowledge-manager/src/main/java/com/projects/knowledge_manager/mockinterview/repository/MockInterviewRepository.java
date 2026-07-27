package com.projects.knowledge_manager.mockinterview.repository;

import com.projects.knowledge_manager.mockinterview.entity.MockInterview;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {

  @Query(
      """
      SELECT DISTINCT i FROM MockInterview i
      LEFT JOIN FETCH i.items
      WHERE i.id = :id
      """)
  Optional<MockInterview> findDetailedById(@Param("id") Long id);

  @Query(
      """
      SELECT DISTINCT i FROM MockInterview i
      LEFT JOIN FETCH i.items
      WHERE i.profileId = :profileId
      ORDER BY i.startedAt DESC
      """)
  List<MockInterview> findDetailedByProfileIdOrderByStartedAtDesc(
      @Param("profileId") Long profileId);

  List<MockInterview> findAllByOrderByStartedAtDesc();

  long countByFinishedAtIsNotNull();

  @Query(
      """
      SELECT COALESCE(SUM(i.totalDurationSeconds), 0)
      FROM MockInterview i
      WHERE i.finishedAt IS NOT NULL
      """)
  long sumFinishedDurationSeconds();

  @Query(
      """
      SELECT COALESCE(AVG(i.totalDurationSeconds), 0)
      FROM MockInterview i
      WHERE i.finishedAt IS NOT NULL
      """)
  Double averageFinishedDurationSeconds();

  @Query(
      """
      SELECT COALESCE(AVG(item.durationSeconds), 0)
      FROM MockInterviewItem item
      WHERE item.completedAt IS NOT NULL
      """)
  Double averageQuestionDurationSeconds();

  @Query(
      """
      SELECT COUNT(i)
      FROM MockInterview i
      WHERE i.finishedAt IS NOT NULL
        AND i.startedAt >= :start
      """)
  long countFinishedSince(@Param("start") Instant start);

  @Query(
      """
      SELECT COALESCE(MAX(i.totalDurationSeconds), 0)
      FROM MockInterview i
      WHERE i.finishedAt IS NOT NULL
      """)
  int maxFinishedDurationSeconds();

  @Query(
      """
      SELECT COALESCE(MIN(i.totalDurationSeconds), 0)
      FROM MockInterview i
      WHERE i.finishedAt IS NOT NULL
        AND i.totalQuestions > 0
      """)
  int minFinishedDurationSeconds();

  @Query(
      """
      SELECT COALESCE(SUM(i.totalQuestions), 0)
      FROM MockInterview i
      WHERE i.finishedAt IS NOT NULL
      """)
  long sumFinishedQuestions();
}

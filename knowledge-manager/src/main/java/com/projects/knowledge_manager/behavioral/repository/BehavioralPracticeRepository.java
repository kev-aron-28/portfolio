package com.projects.knowledge_manager.behavioral.repository;

import com.projects.knowledge_manager.behavioral.entity.BehavioralPractice;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BehavioralPracticeRepository extends JpaRepository<BehavioralPractice, Long> {

  @Query(
      """
      SELECT p FROM BehavioralPractice p
      JOIN FETCH p.question q
      WHERE q.id = :questionId
      ORDER BY p.practiceDate DESC, p.id DESC
      """)
  List<BehavioralPractice> findByQuestionIdOrderByPracticeDateDescIdDesc(
      @Param("questionId") Long questionId);

  @Query(
      """
      SELECT AVG(p.durationSeconds)
      FROM BehavioralPractice p
      """)
  Double averageDurationSeconds();

  @Query(
      """
      SELECT MAX(p.practiceDate)
      FROM BehavioralPractice p
      """)
  Optional<LocalDate> findLastPracticeDate();
}

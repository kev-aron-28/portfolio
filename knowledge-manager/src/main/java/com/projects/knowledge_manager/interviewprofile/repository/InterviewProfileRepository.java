package com.projects.knowledge_manager.interviewprofile.repository;

import com.projects.knowledge_manager.interviewprofile.entity.InterviewProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewProfileRepository extends JpaRepository<InterviewProfile, Long> {

  List<InterviewProfile> findAllByArchivedFalseOrderByNameAsc();

  List<InterviewProfile> findAllByOrderByNameAsc();

  boolean existsByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

  @Query(
      """
      SELECT DISTINCT p FROM InterviewProfile p
      LEFT JOIN FETCH p.targetDifficulties
      WHERE p.id = :id
      """)
  Optional<InterviewProfile> findWithDifficultiesById(@Param("id") Long id);

  @Query(
      """
      SELECT DISTINCT p FROM InterviewProfile p
      LEFT JOIN FETCH p.problems
      WHERE p.id = :id
      """)
  Optional<InterviewProfile> findWithProblemsById(@Param("id") Long id);

  @Query(
      """
      SELECT DISTINCT p FROM InterviewProfile p
      LEFT JOIN FETCH p.behavioralQuestions
      WHERE p.id = :id
      """)
  Optional<InterviewProfile> findWithBehavioralById(@Param("id") Long id);

  @Query(
      """
      SELECT DISTINCT p FROM InterviewProfile p
      LEFT JOIN FETCH p.systemDesignProblems
      WHERE p.id = :id
      """)
  Optional<InterviewProfile> findWithSystemDesignById(@Param("id") Long id);
}

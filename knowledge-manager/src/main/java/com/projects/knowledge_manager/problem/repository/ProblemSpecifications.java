package com.projects.knowledge_manager.problem.repository;

import com.projects.knowledge_manager.problem.entity.Problem;
import com.projects.knowledge_manager.problem.model.Difficulty;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class ProblemSpecifications {

  private ProblemSpecifications() {}

  public static Specification<Problem> withFilters(
      boolean archived,
      String query,
      Long topicId,
      Difficulty difficulty,
      Long tagId,
      Boolean favorite) {

    return (root, criteriaQuery, criteriaBuilder) -> {
      root.fetch("topic", JoinType.LEFT);
      root.fetch("tags", JoinType.LEFT);

      List<Predicate> predicates = new ArrayList<>();
      predicates.add(criteriaBuilder.equal(root.get("archived"), archived));

      if (query != null && !query.isBlank()) {
        String pattern = "%" + query.trim().toLowerCase() + "%";
        Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern);
        Predicate descriptionMatch =
            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
        predicates.add(criteriaBuilder.or(titleMatch, descriptionMatch));
      }

      if (topicId != null) {
        predicates.add(criteriaBuilder.equal(root.get("topic").get("id"), topicId));
      }

      if (difficulty != null) {
        predicates.add(criteriaBuilder.equal(root.get("difficulty"), difficulty));
      }

      if (tagId != null) {
        Join<Object, Object> tags = root.join("tags");
        predicates.add(criteriaBuilder.equal(tags.get("id"), tagId));
      }

      if (favorite != null) {
        predicates.add(criteriaBuilder.equal(root.get("favorite"), favorite));
      }

      if (criteriaQuery != null) {
        criteriaQuery.distinct(true);
      }

      return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    };
  }
}

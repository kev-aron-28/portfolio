package com.projects.job_tracker.infrastructure.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.job_tracker.infrastructure.persistence.entity.CompanyEntity;

public interface JpaCompanyRepository extends JpaRepository<CompanyEntity, Long> {

	Optional<CompanyEntity> findByName(String name);
}

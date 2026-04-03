package com.projects.api_service.infra.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.api_service.infra.entities.UrlEntity;

@Repository
public interface JpaUrlRepository extends JpaRepository<UrlEntity, Long> {
    
}

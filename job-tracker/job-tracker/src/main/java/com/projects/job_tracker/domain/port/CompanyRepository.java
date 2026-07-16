package com.projects.job_tracker.domain.port;

import java.util.Optional;

import com.projects.job_tracker.domain.model.Company;

public interface CompanyRepository {

	Company save(Company company);

	Optional<Company> findByName(String name);
}

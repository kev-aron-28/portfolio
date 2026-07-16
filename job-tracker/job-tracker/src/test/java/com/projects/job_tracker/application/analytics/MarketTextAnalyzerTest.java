package com.projects.job_tracker.application.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.projects.job_tracker.application.analytics.MarketTextAnalyzer.JobTextFields;
import com.projects.job_tracker.domain.model.MarketInsights;

class MarketTextAnalyzerTest {

	@Test
	void detectsSeniorityFromTitle() {
		assertThat(MarketTextAnalyzer.detectExperienceLevel(new JobTextFields("Senior Java Developer", null, null)))
				.isEqualTo("Senior");
		assertThat(MarketTextAnalyzer.detectExperienceLevel(new JobTextFields("Desarrollador Junior", null, null)))
				.isEqualTo("Junior");
	}

	@Test
	void detectsYearsOfExperience() {
		assertThat(MarketTextAnalyzer.detectExperienceLevel(
				new JobTextFields("Backend", "Mínimo 5 años de experiencia en Java", null)))
				.isEqualTo("Senior (5+ años)");
	}

	@Test
	void countsTechnologiesFromRequirements() {
		var jobs = List.of(
				new JobTextFields("Java Dev", "Spring Boot, PostgreSQL, Docker", null),
				new JobTextFields("Frontend", "React, TypeScript", null));

		var top = MarketTextAnalyzer.topTechnologies(jobs, 5);

		assertThat(top).extracting("label").contains("Java", "Spring Boot", "React");
		assertThat(top.getFirst().count()).isGreaterThan(0);
	}

	@Test
	void groupsExperienceLevels() {
		var jobs = List.of(
				new JobTextFields("Senior Engineer", null, null),
				new JobTextFields("Junior Analyst", null, null),
				new JobTextFields("Consultor", null, null));

		var levels = MarketTextAnalyzer.countExperienceLevels(jobs);

		assertThat(levels).containsEntry("Senior", 1L).containsEntry("Junior", 1L).containsEntry("No especificado", 1L);
	}
}

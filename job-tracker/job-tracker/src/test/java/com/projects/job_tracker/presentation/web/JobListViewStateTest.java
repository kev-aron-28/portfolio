package com.projects.job_tracker.presentation.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class JobListViewStateTest {

	@Test
	void listPathOmitsBlankValues() {
		JobListViewState state = new JobListViewState(
				null, null, "  ", null, null, null, null, null, null, null, false, null, null, null, null, null);

		assertThat(state.listPath()).isEqualTo("/jobs");
		assertThat(state.redirectToList()).isEqualTo("redirect:/jobs");
	}

	@Test
	void listPathKeepsGroupingFiltersAndView() {
		JobListViewState state = new JobListViewState(
				"java",
				"linkedin",
				null,
				null,
				new BigDecimal("40000"),
				null,
				null,
				null,
				null,
				"APPLIED",
				true,
				3L,
				"CREATED_AT",
				"DESC",
				"technology",
				"grouped");

		assertThat(state.listPath()).isEqualTo(
				"/jobs?keyword=java&source=linkedin&minSalary=40000&applicationStatus=APPLIED&onlyUnapplied=true&segmentId=3&sortBy=CREATED_AT&sortDirection=DESC&groupBy=technology&view=grouped");
	}
}

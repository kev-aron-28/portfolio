package com.projects.job_tracker.application.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class MarketInsightsBuilderTest {

	@Test
	void buildsSalaryAndDistributionMetrics() {
		List<Object[]> salaryRows = List.<Object[]>of(
				new Object[] { "remote", BigDecimal.valueOf(30000), BigDecimal.valueOf(40000) },
				new Object[] { "onsite", BigDecimal.valueOf(25000), BigDecimal.valueOf(35000) });
		List<Object[]> workModeRows = List.<Object[]>of(
				new Object[] { "remote", 2L },
				new Object[] { "onsite", 1L });
		List<Object[]> companyRows = List.<Object[]>of(
				new Object[] { "Acme", 2L },
				new Object[] { "Beta", 1L });
		List<Object[]> textRows = List.<Object[]>of(
				new Object[] { "Java Developer", "Spring Boot, 3 años", "Backend services" });

		var market = MarketInsightsBuilder.build(
				3,
				2,
				workModeRows,
				List.<Object[]>of(new Object[] { "TI", 3L }),
				List.<Object[]>of(new Object[] { "Tiempo completo", 3L }),
				companyRows,
				List.<Object[]>of(new Object[] { "CDMX", 2L }, new Object[] { "GDL", 1L }),
				salaryRows,
				textRows);

		assertThat(market.salary().jobsWithSalary()).isEqualTo(2);
		assertThat(market.salary().medianMidpoint()).isEqualByComparingTo(BigDecimal.valueOf(35000));
		assertThat(market.jobsByWorkMode()).containsEntry("Remoto", 2L).containsEntry("Presencial", 1L);
		assertThat(market.topCompanies().getFirst().label()).isEqualTo("Acme");
		assertThat(market.topTechnologies()).isNotEmpty();
		assertThat(market.distinctCompanies()).isEqualTo(2);
	}

	@Test
	void returnsEmptyInsightsWhenNoJobs() {
		var market = MarketInsightsBuilder.build(0, 0, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());

		assertThat(market.salary().jobsWithSalary()).isZero();
		assertThat(market.topCompanies()).isEmpty();
	}
}

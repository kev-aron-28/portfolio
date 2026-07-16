package com.projects.job_tracker.application.scraping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.domain.model.ScrapingSettings;
import com.projects.job_tracker.domain.port.ScrapingSettingsRepository;
import com.projects.job_tracker.infrastructure.scraping.ScrapingSettingsHolder;

@ExtendWith(MockitoExtension.class)
class UpdateScrapingSettingsUseCaseTest {

	@Mock
	private ScrapingSettingsRepository scrapingSettingsRepository;

	@Mock
	private ScrapingSettingsHolder settingsHolder;

	@InjectMocks
	private UpdateScrapingSettingsUseCase updateScrapingSettingsUseCase;

	@Test
	void savesAndRefreshesHolder() {
		ScrapingSettings updated = new ScrapingSettings(
				2000, 15, List.of(JobPlatform.OCC), "/tmp/linkedin.json", 45000, "/tmp/occ.json", 45000, "/tmp/indeed.json", 45000, null, 45000, false, 120000L);
		when(scrapingSettingsRepository.save(updated)).thenReturn(updated);

		ScrapingSettings result = updateScrapingSettingsUseCase.execute(
				new UpdateScrapingSettingsUseCase.UpdateScrapingSettingsCommand(
						2000, 15, List.of(JobPlatform.OCC), "/tmp/linkedin.json", 45000, "/tmp/occ.json", 45000, "/tmp/indeed.json", 45000, null, 45000, false, 120000L));

		assertThat(result.rateLimitMs()).isEqualTo(2000);
		verify(settingsHolder).refresh(updated);
	}
}

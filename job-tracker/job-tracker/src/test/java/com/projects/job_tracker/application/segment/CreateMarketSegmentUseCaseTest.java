package com.projects.job_tracker.application.segment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.job_tracker.domain.model.MarketSegment;
import com.projects.job_tracker.domain.port.MarketSegmentRepository;

@ExtendWith(MockitoExtension.class)
class CreateMarketSegmentUseCaseTest {

	@Mock
	private MarketSegmentRepository marketSegmentRepository;

	@InjectMocks
	private CreateMarketSegmentUseCase createMarketSegmentUseCase;

	@Test
	void savesSegmentWithTrimmedFields() {
		when(marketSegmentRepository.save(any())).thenAnswer(invocation -> {
			MarketSegment input = invocation.getArgument(0);
			return new MarketSegment(
					1L, input.name(), input.description(), input.keywords(), input.filters(), input.createdAt());
		});

		MarketSegment created = createMarketSegmentUseCase.execute(
				new CreateMarketSegmentUseCase.CreateMarketSegmentCommand(
						"  Backend  ", "  desc  ", " java ", "{\"location\":\"cdmx\"}"));

		ArgumentCaptor<MarketSegment> captor = ArgumentCaptor.forClass(MarketSegment.class);
		verify(marketSegmentRepository).save(captor.capture());
		assertThat(captor.getValue().name()).isEqualTo("Backend");
		assertThat(captor.getValue().keywords()).isEqualTo("java");
		assertThat(created.id()).isEqualTo(1L);
		assertThat(created.createdAt()).isBeforeOrEqualTo(Instant.now());
	}
}

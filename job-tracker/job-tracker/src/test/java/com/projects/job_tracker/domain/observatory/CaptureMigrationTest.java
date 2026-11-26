package com.projects.job_tracker.domain.observatory;

import static org.assertj.core.api.Assertions.assertThat;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import com.projects.job_tracker.infrastructure.observatory.JdbcMarketPostingRepository;

class CaptureMigrationTest {
    @Test void preservesExistingEvidenceAndCorrectionsWithoutInventingRunsOrNewDates() {
        var source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:captureMigration"+UUID.randomUUID()+";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        source.setUser("sa");
        Flyway.configure().dataSource(source).locations("classpath:db/migration").target("12").load().migrate();
        var jdbc = new JdbcTemplate(source);
        UUID id = UUID.randomUUID();
        Instant observed = Instant.parse("2026-01-01T00:00:00Z"), recorded = observed.plusSeconds(3600);
        jdbc.update("INSERT INTO observatory_postings(id,source,original_text,observed_at,recorded_at) VALUES (?, 'manual', 'Texto histórico', ?, ?)",id,Timestamp.from(observed),Timestamp.from(recorded));
        for (int revision = 1; revision <= 2; revision++) jdbc.update("INSERT INTO observatory_posting_versions(posting_id,revision,recorded_at,reason,method,title) VALUES (?, ?, ?, 'Transcripción', 'MANUAL_V1', ?)",id,revision,Timestamp.from(recorded.plusSeconds(revision)),"Título v"+revision);
        Flyway.configure().dataSource(source).locations("classpath:db/migration").target("13").load().migrate();
        var posting = new JdbcMarketPostingRepository(jdbc).find(id).orElseThrow();
        assertThat(posting.observations()).hasSize(1);
        assertThat(posting.observations().getFirst().observedAt()).isEqualTo(observed);
        assertThat(posting.observations().getFirst().recordedAt()).isEqualTo(recorded);
        assertThat(posting.observations().getFirst().runId()).isNull();
        assertThat(posting.observations().getFirst().contentHash()).isEqualTo(EvidenceHash.sha256("Texto histórico"));
        assertThat(posting.revisions()).hasSize(2).allMatch(r -> r.observationId().equals(id));
        assertThat(posting.current().data().title()).isEqualTo("Título v2");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_capture_runs",Integer.class)).isZero();
    }
}

package com.projects.job_tracker.domain.observatory;

import static org.assertj.core.api.Assertions.assertThat;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import com.projects.job_tracker.infrastructure.observatory.JdbcCollectionLedger;

class ScheduleMigrationTest {
    @Test void backfillsLivenessFromOriginalStartWithoutActivatingAnyScheduleOrForgingNewHistory() throws Exception {
        var source=new JdbcDataSource(); source.setURL("jdbc:h2:mem:schedule-migration"+UUID.randomUUID()+";MODE=PostgreSQL;DB_CLOSE_DELAY=-1"); source.setUser("sa");
        try (var connection=source.getConnection()) {
            var stable=new SingleConnectionDataSource(connection,true); var jdbc=new JdbcTemplate(stable);
            Flyway.configure().dataSource(stable).locations("classpath:db/migration").target("15").load().migrate();
            UUID segment=UUID.randomUUID(),protocol=UUID.randomUUID(),run=UUID.randomUUID(); Instant start=Instant.parse("2026-01-01T00:00:00Z"); var time=Timestamp.from(start);
            jdbc.update("INSERT INTO observatory_segments(id) VALUES (?)",segment);
            jdbc.update("INSERT INTO observatory_segment_versions(segment_id,revision,name,role_family,reason,recorded_at) VALUES (?,1,'Original','Backend','Registro',?)",segment,time);
            jdbc.update("INSERT INTO observatory_protocols(id) VALUES (?)",protocol);
            jdbc.update("""
                INSERT INTO observatory_protocol_versions(protocol_id,revision,segment_id,segment_revision,name,source,query_text,filters,
                    interval_minutes,result_limit,ordering_rule,pagination,sampling_mode,reason,recorded_at)
                VALUES (?,1,?,1,'Original','manual','Java','Filtro humano',60,20,'Orden humano','Varias páginas','SYSTEMATIC_MANUAL','Registro',?)
                """,protocol,segment,time);
            jdbc.update("INSERT INTO observatory_capture_runs(id,protocol_id,protocol_revision,request_key,started_at,state) VALUES (?,?,1,?,?,'RUNNING')",run,protocol,UUID.randomUUID(),time);
            jdbc.update("INSERT INTO observatory_collection_attempts(run_id,status,search_url,collector_version,started_at) VALUES (?,'RUNNING','https://example.com/jobs','ORIGINAL_V1',?)",run,time);
            Flyway.configure().dataSource(stable).locations("classpath:db/migration").target("16").load().migrate();
            var ledger=new JdbcCollectionLedger(jdbc); var attempt=ledger.attempt(run).orElseThrow();
            assertThat(attempt.startedAt()).isEqualTo(start); assertThat(attempt.heartbeatAt()).isEqualTo(start); assertThat(attempt.terminationKind()).isNull();
            assertThat(ledger.staleAttempts(start.plusSeconds(1),start.minusSeconds(1))).containsExactly(run);
            assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_schedules",Integer.class)).isZero();
            assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_schedule_tasks",Integer.class)).isZero();
        }
    }
}

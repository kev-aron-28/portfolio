package com.projects.job_tracker.domain.observatory;

import static org.assertj.core.api.Assertions.assertThat;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import com.projects.job_tracker.infrastructure.observatory.JdbcCaptureRepository;

class CollectionMigrationTest {
    @Test void oldManualProtocolsRemainManualWithoutInventedCollectionSettings() throws Exception {
        var source=new JdbcDataSource(); source.setURL("jdbc:h2:mem:collection-migration"+UUID.randomUUID()+";MODE=PostgreSQL;DB_CLOSE_DELAY=-1"); source.setUser("sa");
        try (var connection=source.getConnection()) {
            var stable=new SingleConnectionDataSource(connection,true);
            Flyway.configure().dataSource(stable).locations("classpath:db/migration").target("14").load().migrate();
            var jdbc=new JdbcTemplate(stable); UUID segment=UUID.randomUUID(),protocol=UUID.randomUUID(); var time=Timestamp.from(Instant.parse("2026-01-01T00:00:00Z"));
            jdbc.update("INSERT INTO observatory_segments(id) VALUES (?)",segment);
            jdbc.update("INSERT INTO observatory_segment_versions(segment_id,revision,name,role_family,reason,recorded_at) VALUES (?,1,'Original','Backend','Registro',?)",segment,time);
            jdbc.update("INSERT INTO observatory_protocols(id) VALUES (?)",protocol);
            jdbc.update("""
                INSERT INTO observatory_protocol_versions(protocol_id,revision,segment_id,segment_revision,name,source,query_text,filters,
                    interval_minutes,result_limit,ordering_rule,pagination,sampling_mode,reason,recorded_at)
                VALUES (?,1,?,1,'Original','manual','Java','Filtro humano',60,20,'Orden humano','Varias páginas','SYSTEMATIC_MANUAL','Registro',?)
                """,protocol,segment,time);
            Flyway.configure().dataSource(stable).locations("classpath:db/migration").target("15").load().migrate();
            var old=new JdbcCaptureRepository(jdbc).protocolHistory(protocol).getFirst();
            assertThat(old.spec().collectionPlan()).isNull(); assertThat(old.spec().filters()).isEqualTo("Filtro humano");
            assertThat(old.spec().samplingMode()).isEqualTo(CaptureModels.SamplingMode.SYSTEMATIC_MANUAL);
            assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_collection_attempts",Integer.class)).isZero();
        }
    }
}

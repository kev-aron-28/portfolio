package com.projects.job_tracker.domain.observatory;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.*;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import com.projects.job_tracker.application.observatory.DuplicateService;
import com.projects.job_tracker.infrastructure.observatory.*;

class DuplicateMigrationTest {
    @Test void preservesLegacyUrlConflictsAsCandidatesWithoutMergingOrDeleting() throws Exception {
        var source=new JdbcDataSource(); source.setURL("jdbc:h2:mem:duplicates"+UUID.randomUUID()+";MODE=PostgreSQL;DB_CLOSE_DELAY=-1"); source.setUser("sa");
        try (var connection=source.getConnection()) {
        // Keep the migration session alive while checking legacy rows (H2 constant-set CHECK expressions).
        var stable=new org.springframework.jdbc.datasource.SingleConnectionDataSource(connection,true);
        Flyway.configure().dataSource(stable).locations("classpath:db/migration").target("13").load().migrate();
        var jdbc=new JdbcTemplate(stable); var postings=new JdbcMarketPostingRepository(jdbc);
        for (int i=0;i<2;i++) postings.insert(new MarketPosting(UUID.randomUUID(),"portal","https://example.com/job?id=1&utm_source="+i,
            "Texto anterior",Instant.parse("2026-01-01T00:00:00Z"),Instant.parse("2026-01-02T00:00:00Z"),
            List.of(new MarketPosting.Revision(1,Instant.parse("2026-01-02T00:00:00Z"),"Original","MANUAL_V1",new PostingData("Java",null,null,null,null,null,null,null,null)))));
        Flyway.configure().dataSource(stable).locations("classpath:db/migration").target("14").load().migrate();
        var repository=new JdbcDuplicateRepository(jdbc); var service=new DuplicateService(repository,postings);
        assertThat(repository.claimsBySource("portal")).hasSize(2);
        assertThat(service.candidates(25,0)).hasSize(1);
        assertThat(service.candidates(25,0).getFirst().signals()).contains("SOURCE_URL");
        assertThat(service.groups()).isEmpty();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_observations",Integer.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_duplicate_reviews",Integer.class)).isZero();
        }
    }
}

package com.projects.job_tracker.presentation.api;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.projects.job_tracker.application.observatory.MarketPostingService;
import com.projects.job_tracker.domain.observatory.*;

@SpringBootTest
@ActiveProfiles("test")
class ObservatoryIdentityConcurrencyTest {
    @Autowired MarketPostingService service;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager transactionManager;
    @Test void simultaneousRegistrationsShareTheIdentityLockAndOnlyOneCommits() throws Exception {
        String source="concurrency-"+UUID.randomUUID();
        var request=new MarketPostingService.Registration(source,"https://example.com/parallel-job","Concurrent evidence",
            Instant.now().minusSeconds(60),new PostingData("Concurrent role",null,null,null,null,null,null,null,null),"same-id",null);
        var gate=new CountDownLatch(1);
        Callable<Object> action=() -> {
            gate.await();
            try { return service.register(request).id(); }
            catch (DuplicateIdentityException ex) { return ex; }
        };
        try (var executor=Executors.newFixedThreadPool(2)) {
            var first=executor.submit(action); var second=executor.submit(action); gate.countDown();
            var results=List.of(first.get(15,TimeUnit.SECONDS),second.get(15,TimeUnit.SECONDS));
            assertThat(results.stream().filter(UUID.class::isInstance).count()).isEqualTo(1);
            assertThat(results.stream().filter(DuplicateIdentityException.class::isInstance).count()).isEqualTo(1);
            assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_postings WHERE source = ?",Integer.class,source)).isEqualTo(1);
        } finally {
            // These worker transactions commit independently, so clean their isolated test rows explicitly.
            new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
                for (UUID id : jdbc.queryForList("SELECT id FROM observatory_postings WHERE source = ?",UUID.class,source)) {
                    jdbc.update("UPDATE observatory_observations SET data_revision = NULL WHERE posting_id = ?",id);
                    jdbc.update("DELETE FROM observatory_posting_versions WHERE posting_id = ?",id);
                    jdbc.update("DELETE FROM observatory_observations WHERE posting_id = ?",id);
                    jdbc.update("DELETE FROM observatory_source_postings WHERE posting_id = ?",id);
                    jdbc.update("DELETE FROM observatory_postings WHERE id = ?",id);
                }
            });
        }
    }
}

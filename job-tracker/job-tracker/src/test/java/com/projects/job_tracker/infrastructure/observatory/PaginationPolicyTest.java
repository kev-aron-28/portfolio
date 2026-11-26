package com.projects.job_tracker.infrastructure.observatory;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.infrastructure.observatory.collection.PaginationPolicy;

class PaginationPolicyTest {
    private final PaginationPolicy policy=new PaginationPolicy();
    @Test void followsObservedLinksKeepingTheQueryForAllPortals() {
        for (var platform:JobPlatform.values()) {
            String cursor=switch(platform) { case INDEED, LINKEDIN -> "start"; case OCC -> "page"; case COMPUTRABAJO -> "p"; };
            String base="https://example.test/jobs?q=Java&remote=1";
            var next=policy.inspect(platform,base,base,"<a rel='next' href='?q=Java&amp;remote=1&amp;"+cursor+"=2'>Siguiente</a>");
            assertThat(next.outcome()).isEqualTo("NEXT_LINK_OBSERVED");
            assertThat(next.url()).contains(cursor+"=2");
        }
    }
    @Test void absentControlsDoNotProveExhaustion() {
        assertThat(policy.inspect(JobPlatform.INDEED,"https://example.test/jobs","https://example.test/jobs","<p>No controls</p>").outcome()).isEqualTo("NAVIGATION_UNKNOWN");
    }
    @Test void explicitDisabledNextIsRecordedSeparatelyFromMissingNavigation() {
        var next=policy.inspect(JobPlatform.INDEED,"https://example.test/jobs","https://example.test/jobs","<nav><button aria-label='Next page' disabled>Siguiente</button></nav>");
        assertThat(next.outcome()).isEqualTo("END_CONTROL_OBSERVED"); assertThat(next.url()).isNull();
    }
    @Test void rejectsChangedFiltersHostsPathsAndNonNumericCursors() {
        String base="https://example.test/jobs?q=Java&remote=1";
        for (String target:new String[]{"https://evil.test/jobs?q=Java&remote=1&start=2","/login?q=Java&remote=1","?q=Python&remote=1&start=2","?q=Java&start=2","?q=Java&remote=1&start=abc","javascript:alert(1)","//user@example.test/jobs?q=Java&remote=1&start=2"}) {
            assertThat(policy.inspect(JobPlatform.INDEED,base,base,"<a rel='next' href='"+target+"'>Next</a>").url()).as(target).isNull();
        }
    }
    @Test void cannotGuessAButtonTargetOrChooseAmongAmbiguousLinks() {
        String base="https://example.test/jobs";
        assertThat(policy.inspect(JobPlatform.LINKEDIN,base,base,"<div class='artdeco-pagination'><button aria-label='Next'>Next</button></div>").outcome()).isEqualTo("UNSUPPORTED_NAVIGATION");
        assertThat(policy.inspect(JobPlatform.OCC,base,base,"<a rel='next' href='?page=2'>Next</a><a rel='next' href='?page=3'>Next</a>").outcome()).isEqualTo("NAVIGATION_UNKNOWN");
    }
    @Test void queryOrderAndEncodingDoNotChangeTheSearchButDuplicateFiltersDo() {
        assertThat(policy.sameSearch(JobPlatform.INDEED,"https://example.test/jobs?q=Java+Senior&remote=1","https://example.test/jobs?remote=1&q=Java%20Senior&start=10")).isTrue();
        assertThat(policy.sameSearch(JobPlatform.INDEED,"https://example.test/jobs?q=Java","https://example.test/jobs?q=Java&q=Python&start=10")).isFalse();
    }
}

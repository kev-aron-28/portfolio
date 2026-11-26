package com.projects.job_tracker.domain.observatory;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class SourceIdentityKeyTest {
    @Test void normalizesHostDefaultPortAndKnownTrackingParameters() {
        assertThat(SourceIdentityKey.url("https://EXAMPLE.com:443/job?id=42&utm_source=a&fbclid=b"))
            .isEqualTo("https://example.com/job?id=42");
        assertThat(SourceIdentityKey.source(" LinkedIn ")).isEqualTo("linkedin");
    }
    @Test void preservesIdentityParametersPathsFragmentsAndProtocol() {
        assertThat(SourceIdentityKey.url("https://example.com/#/jobs/42")).isNotEqualTo(SourceIdentityKey.url("https://example.com/#/jobs/43"));
        assertThat(SourceIdentityKey.url("http://example.com/job")).isNotEqualTo(SourceIdentityKey.url("https://example.com/job"));
        assertThat(SourceIdentityKey.url("https://example.com/Job?id=1")).isNotEqualTo(SourceIdentityKey.url("https://example.com/job?id=1"));
        assertThat(SourceIdentityKey.url("https://example.com/job?id=1")).isNotEqualTo(SourceIdentityKey.url("https://example.com/job?id=2"));
        assertThat(SourceIdentityKey.externalId(" AbC ")).isEqualTo("AbC").isNotEqualTo(SourceIdentityKey.externalId("abc"));
    }
}

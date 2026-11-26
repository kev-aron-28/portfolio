package com.projects.job_tracker.presentation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ObservatoryContextIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private static final String BASE = "/api/observatory/postings";
    private String context() { return """
        {"publishedAt":"2026-01-01T12:00:00Z","countryCode":"mx","region":"Jalisco","city":"Guadalajara",
         "workMode":"ONSITE","weeklyHours":40,"contractType":"PERMANENT","seniority":"SENIOR",
         "evidence":"MX Jalisco Guadalajara, presencial, indefinido, senior, 40 horas"}
        """; }
    private String data(String context) { return """
        {"title":"Java backend","salaryMin":30000,"currency":"MXN","period":"MONTH","taxBasis":"GROSS","context":%s}
        """.formatted(context); }
    private String registration(String context) { return """
        {"source":"manual","originalText":"Publicado el 1 de enero. MX Jalisco Guadalajara, presencial, indefinido, senior, 40 horas",
         "observedAt":"2026-01-02T00:00:00Z","data":%s}
        """.formatted(data(context)); }
    @Test void persistsContextAndKeepsPublicationSeparateFromObservationAndVersions() throws Exception {
        String location = mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(registration(context())))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.revisions[0].data.context.countryCode").value("MX"))
            .andExpect(jsonPath("$.revisions[0].data.quality.salaryCohortReady").value(true))
            .andExpect(jsonPath("$.revisions[0].data.quality.publicationDateKnown").value(true))
            .andReturn().getResponse().getHeader("Location");
        mvc.perform(get(location)).andExpect(jsonPath("$.revisions[0].data.context.weeklyHours").value(40))
            .andExpect(jsonPath("$.observedAt").value("2026-01-02T00:00:00Z"))
            .andExpect(jsonPath("$.revisions[0].data.context.publishedAt").value("2026-01-01T12:00:00Z"));
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).content(
            "{\"expectedRevision\":1,\"reason\":\"Quitar dato no confirmado\",\"data\":" + data(context().replace("\"weeklyHours\":40", "\"weeklyHours\":null")) + "}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.revisions[0].data.context.weeklyHours").value(40))
            .andExpect(jsonPath("$.revisions[1].data.context.weeklyHours").isEmpty())
            .andExpect(jsonPath("$.revisions[1].data.quality.salaryCohortReady").value(false));
    }
    @Test void rejectsInvalidContextWithoutCreatingRows() throws Exception {
        long count = jdbc.queryForObject("SELECT count(*) FROM observatory_postings", Long.class);
        for (String invalid : List.of(
                context().replace("\"mx\"","\"ZZ\""), context().replace("\"weeklyHours\":40","\"weeklyHours\":169"),
                context().replace("\"weeklyHours\":40","\"weeklyHours\":0"), context().replace("\"weeklyHours\":40","\"weeklyHours\":40.001"),
                context().replace("2026-01-01T12:00:00Z","2026-01-03T12:00:00Z"),
                context().replace("MX Jalisco Guadalajara, presencial, indefinido, senior, 40 horas", "cita inventada"),
                "{\"countryCode\":\"MX\"}",
                "{\"workMode\":\"ONSITE\",\"remoteScope\":\"WORLDWIDE\",\"evidence\":\"MX\"}",
                "{\"workMode\":\"REMOTE\",\"remoteScope\":\"RESTRICTED\",\"evidence\":\"MX\"}",
                "{\"workMode\":\"REMOTE\",\"remoteScope\":\"COUNTRY_ONLY\",\"evidence\":\"MX\"}")) {
            mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(registration(invalid)))
                .andExpect(status().isBadRequest());
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_postings", Long.class)).isEqualTo(count);
    }
    @Test void legacyRequestsRemainUnknownAndCorrectionsCannotIntroduceFuturePublication() throws Exception {
        String location = mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(registration("null")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.revisions[0].data.context.countryCode").isEmpty())
            .andExpect(jsonPath("$.revisions[0].data.quality.salaryCohortReady").value(false))
            .andExpect(jsonPath("$.revisions[0].data.quality.publicationDateKnown").value(false))
            .andReturn().getResponse().getHeader("Location");
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).content(
            "{\"expectedRevision\":1,\"reason\":\"Fecha incorrecta\",\"data\":" + data(context().replace("2026-01-01T12:00:00Z", "2026-01-03T12:00:00Z")) + "}"))
            .andExpect(status().isBadRequest());
        mvc.perform(get(location)).andExpect(jsonPath("$.revisions.length()").value(1));
    }
}

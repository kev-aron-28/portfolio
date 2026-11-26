package com.projects.job_tracker.presentation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
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
class ObservatoryControllerIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private static final String BASE = "/api/observatory/postings";
    private String registration(String data) {
        return """
            {"source":"manual", "url":"https://example.com/role", "originalText":"Java junior desde 25000",
             "observedAt":"2026-01-01T12:00:00Z", "data":%s}
            """.formatted(data);
    }
    private String create() throws Exception {
        var response = mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON)
            .content(registration("{\"title\":\"Java junior\",\"salaryMin\":25000}")))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.revisions[0].data.company").isEmpty())
            .andExpect(jsonPath("$.revisions[0].data.currency").isEmpty())
            .andReturn().getResponse();
        return response.getHeader("Location");
    }
    @Test void keepsOriginalHistoryAndDoesNotCreateTrackingData() throws Exception {
        long jobs = jdbc.queryForObject("select count(*) from jobs", Long.class);
        long applications = jdbc.queryForObject("select count(*) from applications", Long.class);
        long companies = jdbc.queryForObject("select count(*) from companies", Long.class);
        String location = create();
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).content("""
            {"expectedRevision":1,"reason":"Corregir transcripción", "data":{"title":"Java backend","salaryMin":26000}}
            """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.originalText").value("Java junior desde 25000"))
            .andExpect(jsonPath("$.revisions.length()").value(2))
            .andExpect(jsonPath("$.revisions[0].data.salaryMin").value(25000))
            .andExpect(jsonPath("$.revisions[1].data.salaryMin").value(26000))
            .andExpect(jsonPath("$.revisions[1].method").value("HUMAN_CORRECTION_V1"));
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).content("""
            {"expectedRevision":1,"reason":"Edición obsoleta", "data":{"title":"No guardar"}}
            """ )).andExpect(status().isBadRequest());
        mvc.perform(get(location)).andExpect(jsonPath("$.revisions.length()").value(2));
        mvc.perform(get(BASE)).andExpect(status().isOk());
        assertThat(jdbc.queryForObject("select count(*) from jobs", Long.class)).isEqualTo(jobs);
        assertThat(jdbc.queryForObject("select count(*) from applications", Long.class)).isEqualTo(applications);
        assertThat(jdbc.queryForObject("select count(*) from companies", Long.class)).isEqualTo(companies);
    }
    @Test void rejectsInvalidSalariesAndDoesNotPersistInvalidRegistrations() throws Exception {
        long before = jdbc.queryForObject("select count(*) from observatory_postings", Long.class);
        for (String data : new String[]{
            "{\"title\":\"Java\",\"salaryMin\":30000,\"salaryMax\":20000}",
            "{\"title\":\"Java\",\"salaryMin\":-1}",
            "{\"title\":\"Java\",\"currency\":\"INVALID\"}",
            "{\"title\":\"\"}"}) {
            mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(registration(data)))
                .andExpect(status().isBadRequest());
        }
        assertThat(jdbc.queryForObject("select count(*) from observatory_postings", Long.class)).isEqualTo(before);
    }
    @Test void requiresCorrectionReasonAndProvidesNotFoundAndPaginationValidation() throws Exception {
        String location = create();
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).content("""
            {"expectedRevision":1,"reason":" ","data":{"title":"Cambio"}}
            """ )).andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/" + UUID.randomUUID())).andExpect(status().isNotFound());
        mvc.perform(get(BASE + "?limit=101")).andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "?offset=-1")).andExpect(status().isBadRequest());
        mvc.perform(get(location)).andExpect(jsonPath("$.revisions.length()").value(1));
    }
    @Test void rendersObservatoryAndNavigation() throws Exception {
        mvc.perform(get("/observatory")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("posting-form")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("/js/observatory.js")));
    }
}

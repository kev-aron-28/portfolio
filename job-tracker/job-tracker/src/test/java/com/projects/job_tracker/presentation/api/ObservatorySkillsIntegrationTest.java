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
class ObservatorySkillsIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private static final String SKILLS = "/api/observatory/skills";
    private static final String POSTINGS = "/api/observatory/postings";
    private static final String JAVA_ID = "10000000-0000-0000-0000-000000000001";
    private String requirement(String id, int revision, String obligation, String evidence) {
        return """
            {"skillId":"%s","skillRevision":%d,"obligation":"%s","evidence":"%s"}
            """.formatted(id, revision, obligation, evidence);
    }
    private String registration(boolean reviewed, String requirements) {
        return """
            {"source":"manual", "originalText":"Java obligatorio. SQL deseable.",
            "observedAt":"2026-01-01T12:00:00Z", "data":{"title":"Backend",
            "requirementsReviewed":%s,"skillRequirements":%s}}
            """.formatted(reviewed, requirements);
    }
    @Test void versionsCatalogWithoutRewritingPostingHistory() throws Exception {
        String location = mvc.perform(post(POSTINGS).contentType(MediaType.APPLICATION_JSON)
            .content(registration(true, "[" + requirement(JAVA_ID, 1, "REQUIRED", "Java obligatorio") + "]")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.revisions[0].data.quality.skillAnalysisReady").value(true))
            .andReturn().getResponse().getHeader("Location");
        mvc.perform(put(SKILLS + "/" + JAVA_ID).contentType(MediaType.APPLICATION_JSON).content("""
            {"expectedRevision":1,"name":"Java Platform","aliases":["Java SE"],"reason":"Aclarar nombre"}
            """ )).andExpect(status().isOk()).andExpect(jsonPath("$.revision").value(2));
        mvc.perform(get(location)).andExpect(jsonPath("$.revisions[0].data.skillRequirements[0].skillRevision").value(1))
            .andExpect(jsonPath("$.revisions[0].data.skillRequirements[0].obligation").value("REQUIRED"));
        mvc.perform(get(SKILLS + "/" + JAVA_ID + "/history"))
            .andExpect(jsonPath("$[0].name").value("Java")).andExpect(jsonPath("$[1].name").value("Java Platform"));
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).content("""
            {"expectedRevision":1,"reason":"Revisar obligatoriedad","data":{"title":"Backend", "requirementsReviewed":true,
            "skillRequirements":[%s]}}
            """.formatted(requirement(JAVA_ID, 1, "UNKNOWN", "Java obligatorio"))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.revisions[0].data.skillRequirements[0].obligation").value("REQUIRED"))
            .andExpect(jsonPath("$.revisions[1].data.skillRequirements[0].obligation").value("UNKNOWN"));
    }
    @Test void rejectsUnverifiedEvidenceMissingCatalogVersionDuplicatesAndUnreviewedAnnotations() throws Exception {
        long before = jdbc.queryForObject("select count(*) from observatory_postings", Long.class);
        String valid = requirement(JAVA_ID, 1, "DESIRED", "Java obligatorio");
        for (String list : new String[]{
            "[" + requirement(JAVA_ID, 1, "REQUIRED", "Texto inventado") + "]",
            "[" + requirement(UUID.randomUUID().toString(), 1, "UNKNOWN", "Java") + "]",
            "[" + requirement(JAVA_ID, 999, "UNKNOWN", "Java") + "]",
            "[" + valid + "," + valid + "]", "[null]"}) {
            mvc.perform(post(POSTINGS).contentType(MediaType.APPLICATION_JSON).content(registration(true, list)))
                .andExpect(status().isBadRequest());
        }
        mvc.perform(post(POSTINGS).contentType(MediaType.APPLICATION_JSON).content(registration(false, "[" + valid + "]")))
            .andExpect(status().isBadRequest());
        assertThat(jdbc.queryForObject("select count(*) from observatory_postings", Long.class)).isEqualTo(before);
    }
    @Test void distinguishesUnreviewedFromReviewedEmptyAndExplainsSalaryMissingness() throws Exception {
        mvc.perform(post(POSTINGS).contentType(MediaType.APPLICATION_JSON).content(registration(false,"[]")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.revisions[0].data.quality.skillAnalysisReady").value(false))
            .andExpect(jsonPath("$.revisions[0].data.quality.salaryMissingFields.length()").value(9));
        mvc.perform(post(POSTINGS).contentType(MediaType.APPLICATION_JSON).content(registration(true,"[]")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.revisions[0].data.quality.skillAnalysisReady").value(true))
            .andExpect(jsonPath("$.revisions[0].data.skillRequirements").isEmpty());
    }
    @Test void createsAliasesAndRejectsStaleCatalogEditsAndInvalidAliases() throws Exception {
        String location = mvc.perform(post(SKILLS).contentType(MediaType.APPLICATION_JSON).content("""
            {"name":"PostgreSQL","aliases":["Postgres"],"reason":"Agregar habilidad"}
            """ )).andExpect(status().isCreated()).andExpect(jsonPath("$.aliases[0]").value("Postgres"))
            .andReturn().getResponse().getHeader("Location");
        String update = location.replace("/history", "");
        String edit = """
            {"expectedRevision":1,"name":"PostgreSQL","aliases":[],"reason":"Revisar alias"}
            """;
        mvc.perform(put(update).contentType(MediaType.APPLICATION_JSON).content(edit)).andExpect(status().isOk());
        mvc.perform(put(update).contentType(MediaType.APPLICATION_JSON).content(edit)).andExpect(status().isBadRequest());
        mvc.perform(get(location)).andExpect(jsonPath("$.length()").value(2));
        for (String aliases : new String[]{"[null]", "[\" \"]", "[\"POSTGRESQL\"]", "[\"pg\",\"PG\"]"}) {
            mvc.perform(post(SKILLS).contentType(MediaType.APPLICATION_JSON).content(
                "{\"name\":\"PostgreSQL\",\"aliases\":" + aliases + ",\"reason\":\"Revisar\"}"))
                .andExpect(status().isBadRequest());
        }
        mvc.perform(get(SKILLS + "/" + UUID.randomUUID() + "/history")).andExpect(status().isNotFound());
        mvc.perform(get("/observatory")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("skill-requirements")));
    }
}

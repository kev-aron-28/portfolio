package com.projects.job_tracker.presentation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class ObservatoryCaptureIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private static final String BASE = "/api/observatory";
    private String postJson(String path, String payload) throws Exception {
        return mvc.perform(post(BASE + path).contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
    }
    private String id(String json) { return JsonPath.read(json,"$.id"); }
    private String segment() throws Exception {
        return id(postJson("/segments", """
            {"spec":{"name":"Backend México","roleFamily":"Backend","countryCode":"MX"},"reason":"Primera muestra"}
            """));
    }
    private String protocol(String segment, int limit) throws Exception {
        return id(postJson("/protocols", """
            {"spec":{"segmentId":"%s","segmentRevision":1,"name":"Revisión semanal","source":"manual",
            "query":"Java backend","filters":"Sin filtros","intervalMinutes":10080,"resultLimit":%d,
            "ordering":"Más recientes","pagination":"Revisar páginas hasta el límite","samplingMode":"SYSTEMATIC_MANUAL"},"reason":"Protocolo inicial"}
            """.formatted(segment,limit)));
    }
    private String start(String protocol, String key) throws Exception {
        return id(postJson("/protocols/" + protocol + "/runs", "{\"protocolRevision\":1,\"requestKey\":\""+key+"\"}"));
    }
    private String posting() throws Exception {
        return id(postJson("/postings", """
            {"source":"manual","originalText":"Java obligatorio. Desde 30000.","observedAt":"2026-01-01T00:00:00Z",
            "data":{"title":"Java backend","salaryMin":30000}}
            """));
    }
    private String observe(String posting, int expected, String key, String text, String data) {
        return """
            {"postingId":"%s","expectedRevision":%d,"itemKey":"%s","observedAt":"%s","availability":"AVAILABLE",
            "originalText":"%s","data":%s}
            """.formatted(posting,expected,key,Instant.now(),text,data);
    }
    @Test void pinsHistoricalDefinitionsAndRejectsStaleConfigEdits() throws Exception {
        String segment = segment(), protocol = protocol(segment,10);
        String run = start(protocol, UUID.randomUUID().toString());
        String update = """
            {"expectedRevision":1,"spec":{"name":"Frontend","roleFamily":"Frontend","countryCode":"MX"},"reason":"Cambiar universo"}
            """;
        mvc.perform(put(BASE+"/segments/"+segment).contentType(MediaType.APPLICATION_JSON).content(update)).andExpect(status().isOk());
        mvc.perform(put(BASE+"/segments/"+segment).contentType(MediaType.APPLICATION_JSON).content(update)).andExpect(status().isBadRequest());
        mvc.perform(get(BASE+"/protocols/"+protocol+"/history")).andExpect(jsonPath("$[0].spec.segmentRevision").value(1));
        mvc.perform(get(BASE+"/segments/"+segment+"/history")).andExpect(jsonPath("$[0].spec.roleFamily").value("Backend"))
            .andExpect(jsonPath("$[1].spec.roleFamily").value("Frontend"));
        mvc.perform(get(BASE+"/runs/"+run)).andExpect(jsonPath("$.protocolRevision").value(1));
        mvc.perform(put(BASE+"/protocols/"+protocol).contentType(MediaType.APPLICATION_JSON).content("""
            {"expectedRevision":1,"spec":{"segmentId":"%s","segmentRevision":2,"name":"Nuevo protocolo","source":"manual",
            "query":"React","filters":"Sin filtros","intervalMinutes":1440,"resultLimit":5,"ordering":"Más recientes",
            "pagination":"Primera página","samplingMode":"EXPLORATORY"},"reason":"Ajustar captura"}
            """.formatted(segment))).andExpect(status().isOk());
        mvc.perform(get(BASE+"/protocols/"+protocol+"/history")).andExpect(jsonPath("$[0].spec.query").value("Java backend"))
            .andExpect(jsonPath("$[1].spec.segmentRevision").value(2));
        mvc.perform(get(BASE+"/runs/"+run)).andExpect(jsonPath("$.protocolRevision").value(1));
    }
    @Test void retriesAreIdempotentIncludingAfterRunFinishes() throws Exception {
        String protocol = protocol(segment(),10), key = UUID.randomUUID().toString();
        String run = start(protocol,key);
        assertThat(start(protocol,key)).isEqualTo(run);
        String posting = posting();
        String command = observe(posting,1,"item-1","Java obligatorio. Desde 30000.","null");
        String observation = id(postJson("/runs/"+run+"/observations",command));
        assertThat(id(postJson("/runs/"+run+"/observations",command))).isEqualTo(observation);
        String finish = "{\"state\":\"COMPLETE\",\"pagesVisited\":1,\"truncated\":false}";
        mvc.perform(put(BASE+"/runs/"+run+"/finish").contentType(MediaType.APPLICATION_JSON).content(finish)).andExpect(status().isOk());
        mvc.perform(put(BASE+"/runs/"+run+"/finish").contentType(MediaType.APPLICATION_JSON).content(finish)).andExpect(status().isOk());
        assertThat(id(postJson("/runs/"+run+"/observations",command))).isEqualTo(observation);
        assertThat(start(protocol,key)).isEqualTo(run);
        mvc.perform(get(BASE+"/runs/"+run)).andExpect(jsonPath("$.observationCount").value(1));
        mvc.perform(get(BASE+"/postings/"+posting)).andExpect(jsonPath("$.revisions.length()").value(1))
            .andExpect(jsonPath("$.observations.length()").value(2));
        mvc.perform(post(BASE+"/runs/"+run+"/observations").contentType(MediaType.APPLICATION_JSON)
            .content(observe(posting,1,"new-item","Java obligatorio. Desde 30000.","null"))).andExpect(status().isBadRequest());
        mvc.perform(post(BASE+"/runs/"+run+"/observations").contentType(MediaType.APPLICATION_JSON).content(command.replace("Desde 30000", "Desde 40000")))
            .andExpect(status().isBadRequest());
    }
    @Test void changedEvidenceCreatesVersionAndCorrectionsUseThatEvidence() throws Exception {
        long jobs = jdbc.queryForObject("SELECT count(*) FROM jobs",Long.class), apps = jdbc.queryForObject("SELECT count(*) FROM applications",Long.class);
        String run = start(protocol(segment(),10),UUID.randomUUID().toString()), posting = posting();
        postJson("/runs/"+run+"/observations",observe(posting,1,"same","Java obligatorio. Desde 30000.","{\"title\":\"Java backend\",\"salaryMin\":30000.00}"));
        mvc.perform(get(BASE+"/postings/"+posting)).andExpect(jsonPath("$.revisions.length()").value(1));
        String observation = id(postJson("/runs/"+run+"/observations",observe(posting,1,"changed","Docker requerido. Desde 40000.","{\"title\":\"Java backend\",\"salaryMin\":40000}")));
        mvc.perform(get(BASE+"/postings/"+posting)).andExpect(jsonPath("$.originalText").value("Java obligatorio. Desde 30000."))
            .andExpect(jsonPath("$.revisions[1].observationId").value(observation)).andExpect(jsonPath("$.revisions[1].data.salaryMin").value(40000));
        mvc.perform(put(BASE+"/postings/"+posting).contentType(MediaType.APPLICATION_JSON).content("""
            {"expectedRevision":2,"reason":"Transcribir requisito","data":{"title":"Java backend","requirementsReviewed":true,
            "skillRequirements":[{"skillId":"10000000-0000-0000-0000-000000000003","skillRevision":1,"obligation":"REQUIRED","evidence":"Docker requerido"}]}}
            """ )).andExpect(status().isOk()).andExpect(jsonPath("$.revisions[2].observationId").value(observation));
        mvc.perform(get(BASE+"/runs/"+run+"/observations")).andExpect(jsonPath("$[1].dataRevision").value(2));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM jobs",Long.class)).isEqualTo(jobs);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM applications",Long.class)).isEqualTo(apps);
    }
    @Test void validatesChangedContentAvailabilityDatesAndAtomicity() throws Exception {
        String run = start(protocol(segment(),10),UUID.randomUUID().toString()), posting = posting();
        String command = observe(posting,1,"a","Texto diferente", "null");
        mvc.perform(post(BASE+"/runs/"+run+"/observations").contentType(MediaType.APPLICATION_JSON).content(command)).andExpect(status().isBadRequest());
        mvc.perform(post(BASE+"/runs/"+run+"/observations").contentType(MediaType.APPLICATION_JSON)
            .content(command.replace("\"data\":null","\"data\":{\"title\":\"Java\",\"context\":{\"countryCode\":\"MX\",\"evidence\":\"inventada\"}}")))
            .andExpect(status().isBadRequest());
        String valid = observe(posting,1,"b","Java obligatorio. Desde 30000.","null");
        mvc.perform(post(BASE+"/runs/"+run+"/observations").contentType(MediaType.APPLICATION_JSON)
            .content(valid.replaceFirst("\\\"observedAt\\\":\\\"[^\\\"]+\\\"", "\"observedAt\":\"2026-01-01T00:00:00Z\"")))
            .andExpect(status().isBadRequest());
        mvc.perform(post(BASE+"/runs/"+run+"/observations").contentType(MediaType.APPLICATION_JSON)
            .content(valid.replace("AVAILABLE","UNKNOWN"))).andExpect(status().isBadRequest());
        mvc.perform(get(BASE+"/runs/"+run)).andExpect(jsonPath("$.observationCount").value(0));
        mvc.perform(get(BASE+"/postings/"+posting)).andExpect(jsonPath("$.revisions.length()").value(1))
            .andExpect(jsonPath("$.observations.length()").value(1));
    }
    @Test void capturesFailureAndTruncationWithoutTreatingThemAsCompleteSamples() throws Exception {
        String protocol = protocol(segment(),1), run = start(protocol,UUID.randomUUID().toString()), posting = posting();
        postJson("/runs/"+run+"/observations",observe(posting,1,"a","Java obligatorio. Desde 30000.","null"));
        mvc.perform(put(BASE+"/runs/"+run+"/finish").contentType(MediaType.APPLICATION_JSON)
            .content("{\"state\":\"COMPLETE\",\"pagesVisited\":1,\"truncated\":false}")).andExpect(status().isBadRequest());
        mvc.perform(put(BASE+"/runs/"+run+"/finish").contentType(MediaType.APPLICATION_JSON)
            .content("{\"state\":\"PARTIAL\",\"pagesVisited\":1,\"truncated\":true,\"notes\":\"Se alcanzó límite\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("PARTIAL"));
        String failedRun = start(protocol,UUID.randomUUID().toString());
        postJson("/runs/"+failedRun+"/observations", """
            {"postingId":"%s","expectedRevision":1,"itemKey":"fallo","observedAt":"%s","availability":"UNKNOWN"}
            """.formatted(posting,Instant.now()));
        mvc.perform(put(BASE+"/runs/"+failedRun+"/finish").contentType(MediaType.APPLICATION_JSON)
            .content("{\"state\":\"FAILED\",\"pagesVisited\":0,\"truncated\":false,\"notes\":\"Portal inaccesible\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.state").value("FAILED"));
        mvc.perform(get(BASE+"/postings/"+posting)).andExpect(jsonPath("$.revisions.length()").value(1));
    }
    @Test void rejectsMismatchingSourceAndUnknownResultsCannotBeComplete() throws Exception {
        String run = start(protocol(segment(),10),UUID.randomUUID().toString());
        String foreignPosting = id(postJson("/postings", """
            {"source":"otro portal","originalText":"Java","observedAt":"2026-01-01T00:00:00Z","data":{"title":"Java"}}
            """));
        mvc.perform(post(BASE+"/runs/"+run+"/observations").contentType(MediaType.APPLICATION_JSON)
            .content(observe(foreignPosting,1,"wrong-source","Java","null"))).andExpect(status().isBadRequest());
        String posting = posting();
        postJson("/runs/"+run+"/observations", """
            {"postingId":"%s","expectedRevision":1,"itemKey":"unknown","observedAt":"%s","availability":"UNKNOWN"}
            """.formatted(posting,Instant.now()));
        mvc.perform(put(BASE+"/runs/"+run+"/finish").contentType(MediaType.APPLICATION_JSON)
            .content("{\"state\":\"COMPLETE\",\"pagesVisited\":1,\"truncated\":false}")).andExpect(status().isBadRequest());
        mvc.perform(get(BASE+"/runs/"+run)).andExpect(jsonPath("$.state").value("RUNNING"));
    }
    @Test void validatesConfigurationAndMissingResourcesAndRendersCaptureScreen() throws Exception {
        mvc.perform(post(BASE+"/segments").contentType(MediaType.APPLICATION_JSON).content("{\"spec\":{\"name\":\"Mercado\"},\"reason\":\"a\"}"))
            .andExpect(status().isBadRequest());
        mvc.perform(get(BASE+"/segments/"+UUID.randomUUID()+"/history")).andExpect(status().isNotFound());
        mvc.perform(get(BASE+"/runs/"+UUID.randomUUID())).andExpect(status().isNotFound());
        mvc.perform(get(BASE+"/runs?limit=101")).andExpect(status().isBadRequest());
        mvc.perform(get("/observatory/captures")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("protocol-form")));
    }
}

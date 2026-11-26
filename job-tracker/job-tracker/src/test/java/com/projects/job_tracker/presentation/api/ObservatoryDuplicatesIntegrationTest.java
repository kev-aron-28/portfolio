package com.projects.job_tracker.presentation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ObservatoryDuplicatesIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private static final String BASE="/api/observatory";
    private String payload(String source,String url,String externalId,String override) {
        return """
            {"source":"%s","url":%s,"externalId":%s,"identityConflictReason":%s,
             "originalText":"Backend Java publicado","observedAt":"2026-01-01T00:00:00Z",
             "data":{"title":"Backend Java","company":"Acme"}}
            """.formatted(source,quote(url),quote(externalId),quote(override));
    }
    private String quote(String value) { return value==null ? "null" : "\""+value+"\""; }
    private String send(String route,String data) throws Exception {
        return mvc.perform(post(BASE+route).contentType(MediaType.APPLICATION_JSON).content(data)).andExpect(status().is2xxSuccessful())
            .andReturn().getResponse().getContentAsString();
    }
    private String create(String source) throws Exception { return JsonPath.read(send("/postings",payload(source,null,null,null)),"$.id"); }
    private String decide(String a,String b,int expected,String decision,String reason) throws Exception {
        return send("/duplicate-reviews",command(a,b,expected,1,1,decision,reason));
    }
    private String command(String a,String b,int expected,int leftVersion,int rightVersion,String decision,String reason) {
        return """
            {"leftId":"%s","rightId":"%s","expectedRevision":%d,"leftRevision":%d,"rightRevision":%d,"decision":"%s","reason":"%s"}
            """.formatted(a,b,expected,leftVersion,rightVersion,decision,reason);
    }
    @Test void exactLocatorsBlockAccidentalRegistrationButExplicitIndependentReasonIsAudited() throws Exception {
        String a=JsonPath.read(send("/postings",payload("LinkedIn","https://EXAMPLE.com:443/job?id=1&utm_source=test","abc",null)),"$.id");
        long observations=jdbc.queryForObject("SELECT count(*) FROM observatory_observations",Long.class);
        mvc.perform(post(BASE+"/postings").contentType(MediaType.APPLICATION_JSON)
            .content(payload(" linkedin ","https://example.com/job?id=1&fbclid=tracking",null,null)))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.matchingPostingIds[0]").value(a));
        mvc.perform(post(BASE+"/postings").contentType(MediaType.APPLICATION_JSON)
            .content(payload("LINKEDIN","https://example.com/job?id=2","abc",null))).andExpect(status().isConflict());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_observations",Long.class)).isEqualTo(observations);
        String b=JsonPath.read(send("/postings",payload("linkedin","https://example.com/job?id=1",null,"URL reutilizada para otra vacante")),"$.id");
        mvc.perform(get(BASE+"/postings/"+b+"/identity"))
            .andExpect(jsonPath("$.sourceIdentity.registrationNote").value("URL reutilizada para otra vacante"))
            .andExpect(jsonPath("$.memberIds.length()").value(1));
        mvc.perform(get(BASE+"/duplicate-candidates")).andExpect(jsonPath("$[0].signals",org.hamcrest.Matchers.hasItem("SOURCE_URL")));
        decide(a,b,0,"DISTINCT","La empresa reutilizó la URL");
        mvc.perform(get(BASE+"/duplicate-candidates")).andExpect(jsonPath("$.length()").value(0));
    }
    @Test void differentSourcesOrJobParametersRemainIndependentUntilReviewed() throws Exception {
        send("/postings",payload("portal A","https://example.com/job?id=1","same-id",null));
        send("/postings",payload("portal B","https://example.com/job?id=1","same-id",null));
        send("/postings",payload("portal A","https://example.com/job?id=2","other-id",null));
        mvc.perform(get(BASE+"/duplicate-groups")).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get(BASE+"/duplicate-candidates")).andExpect(jsonPath("$.length()").value(3));
    }
    @Test void mergeAndUndoPreserveEvidenceAndHistoricalResolutionAndSupportRetry() throws Exception {
        String a=create("portal A"),b=create("portal B");
        long count=jdbc.queryForObject("SELECT count(*) FROM observatory_observations",Long.class);
        String command=command(a,b,0,1,1,"MERGED","Mismo anuncio en dos portales");
        String review=send("/duplicate-reviews",command),reviewId=JsonPath.read(review,"$.id");
        String at=JsonPath.read(review,"$.recordedAt");
        assertThat((String)JsonPath.read(send("/duplicate-reviews",command),"$.id")).isEqualTo(reviewId);
        mvc.perform(get(BASE+"/postings/"+a+"/identity")).andExpect(jsonPath("$.memberIds.length()").value(2));
        decide(a,b,1,"UNREVIEWED","Se detectó una diferencia importante");
        mvc.perform(get(BASE+"/postings/"+a+"/identity")).andExpect(jsonPath("$.memberIds.length()").value(1));
        mvc.perform(get(BASE+"/postings/"+a+"/identity").param("at",at)).andExpect(jsonPath("$.memberIds.length()").value(2));
        mvc.perform(get(BASE+"/duplicate-reviews/"+reviewId+"/history")).andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].decision").value("MERGED")).andExpect(jsonPath("$[1].decision").value("UNREVIEWED"));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM observatory_observations",Long.class)).isEqualTo(count);
        mvc.perform(get(BASE+"/postings/"+a)).andExpect(jsonPath("$.originalText").value("Backend Java publicado"))
            .andExpect(jsonPath("$.revisions.length()").value(1));
    }
    @Test void preventsContradictionsAndCyclesAndUndoSplitsAGroup() throws Exception {
        String a=create("A"),b=create("B"),c=create("C");
        decide(a,c,0,"DISTINCT","Puestos distintos");
        decide(a,b,0,"MERGED","Coinciden A y B");
        mvc.perform(post(BASE+"/duplicate-reviews").contentType(MediaType.APPLICATION_JSON)
            .content(command(b,c,0,1,1,"MERGED","Intentar unir B y C"))).andExpect(status().isBadRequest());
        decide(a,c,1,"UNREVIEWED","Reconsiderar diferencia");
        decide(b,c,0,"MERGED","Coinciden B y C");
        mvc.perform(get(BASE+"/postings/"+a+"/identity")).andExpect(jsonPath("$.memberIds.length()").value(3));
        mvc.perform(post(BASE+"/duplicate-reviews").contentType(MediaType.APPLICATION_JSON)
            .content(command(a,c,2,1,1,"MERGED","Unión redundante"))).andExpect(status().isBadRequest());
        decide(a,b,1,"UNREVIEWED","Separar A");
        mvc.perform(get(BASE+"/postings/"+a+"/identity")).andExpect(jsonPath("$.memberIds.length()").value(1));
        mvc.perform(get(BASE+"/postings/"+b+"/identity")).andExpect(jsonPath("$.memberIds.length()").value(2));
    }
    @Test void pinsReviewedVersionsAndRequiresReReviewAfterDataChanges() throws Exception {
        String a=create("A"),b=create("B");
        decide(a,b,0,"DISTINCT","Diferencias de datos");
        mvc.perform(put(BASE+"/postings/"+a).contentType(MediaType.APPLICATION_JSON).content("""
            {"expectedRevision":1,"reason":"Corregir título","data":{"title":"Otro puesto","company":"Acme"}}
            """ )).andExpect(status().isOk());
        mvc.perform(get(BASE+"/duplicate-reviews")).andExpect(jsonPath("$[0].postingDataChanged").value(true));
        mvc.perform(post(BASE+"/duplicate-reviews").contentType(MediaType.APPLICATION_JSON)
            .content(command(a,b,1,1,1,"MERGED","Datos obsoletos"))).andExpect(status().isBadRequest());
        mvc.perform(post(BASE+"/duplicate-reviews").contentType(MediaType.APPLICATION_JSON)
            .content(command(a,b,1,2,1,"UNREVIEWED","Volver a revisar datos nuevos"))).andExpect(status().isOk());
        mvc.perform(get(BASE+"/duplicate-reviews")).andExpect(jsonPath("$[0].postingDataChanged").value(false));
        mvc.perform(get(BASE+"/duplicate-candidates?limit=101")).andExpect(status().isBadRequest());
        mvc.perform(get(BASE+"/duplicate-reviews/"+UUID.randomUUID()+"/history")).andExpect(status().isNotFound());
        mvc.perform(get("/observatory/duplicates")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("duplicate-decision-form")));
    }
}

package com.projects.job_tracker.presentation.api;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.jayway.jsonpath.JsonPath;
import com.projects.job_tracker.domain.model.*;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.infrastructure.observatory.collection.*;

@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:collection-tests;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ObservatoryCollectionIntegrationTest.FixtureConfig.class)
public class ObservatoryCollectionIntegrationTest {
    @TestConfiguration public static class FixtureConfig {
        @Bean @Primary public FixtureCollector fixtureCollector(SourceEvidenceParser parser) { return new FixtureCollector(parser); }
    }
    public static class FixtureCollector implements MarketCollector {
        final SourceEvidenceParser parser;
        final AtomicInteger calls=new AtomicInteger();
        volatile String mode="SUCCESS", suffix=UUID.randomUUID().toString().replace("-","");
        volatile CountDownLatch entered,release;
        FixtureCollector(SourceEvidenceParser parser) { this.parser=parser; }
        public String version() { return "FIXTURE_V1"; }
        public String searchUrl(CaptureModels.ProtocolVersion protocol) { return new BrowserMarketCollector(parser,null,null).searchUrl(protocol); }
        public Report collect(CaptureModels.ProtocolVersion protocol,Consumer<Item> sink) {
            assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
            calls.incrementAndGet();
            if (entered!=null) { entered.countDown(); try { if (!release.await(10,TimeUnit.SECONDS)) throw new IllegalStateException("Test timed out"); } catch(InterruptedException ex) { throw new IllegalStateException(ex); } }
            if (mode.equals("EMPTY")) return new Report(searchUrl(protocol),"FIXTURE_V1",1,false,"Sin tarjetas reconocidas, cobertura desconocida");
            try {
                String html=fixture("search-results"),detail=fixture("detail-panel");
                String card=Jsoup.parse(html).select("div.job_seen_beacon").stream().filter(e -> e.text().contains("Capgemini")).findFirst().orElseThrow().outerHtml();
                String oldId=parser.summary(JobPlatform.INDEED,card).orElseThrow().externalId();
                card=card.replace(oldId,suffix);
                var item=parser.parse(JobPlatform.INDEED,0,card,detail,true,Instant.now());
                if (mode.equals("INVALID")) {
                    var j=item.parsed();
                    var invalid=new ScrapedJob(j.platform(),j.externalId(),j.title(),j.companyName(),j.description(),j.location(),new java.math.BigDecimal("-1"),null,j.url(),null,null,null,null,null,null,null);
                    item=new Item(item.itemKey(),item.observedAt(),item.listingHtml(),item.detailHtml(),true,item.originalText(),invalid,"Importe extraído inválido");
                }
                sink.accept(item);
            } catch(java.io.IOException ex) { throw new IllegalStateException(ex); }
            if (mode.equals("THROW")) throw new IllegalStateException("Fixture portal interrupted");
            return new Report(searchUrl(protocol),"FIXTURE_V1",1,false,"Sólo primera página de prueba");
        }
        public Report collect(CaptureModels.ProtocolVersion protocol,Consumer<Item> sink,java.util.function.BooleanSupplier active,Consumer<PageEvidence> pages) {
            if (!mode.equals("PAGE_FAILURE")) return MarketCollector.super.collect(protocol,sink,active,pages);
            pages.accept(new PageEvidence(1,searchUrl(protocol),Instant.now(),"<html>First fixture page</html>",1,EvidenceHash.sha256("page1"),searchUrl(protocol)+"&start=10","NEXT_LINK_OBSERVED"));
            collect(protocol,sink);
            pages.accept(new PageEvidence(2,searchUrl(protocol)+"&start=10",Instant.now(),"<html>Security check</html>",0,EvidenceHash.sha256("page2"),null,"ACCESS_NOT_CONFIRMED"));
            throw new IllegalStateException("Second page blocked after preserving evidence");
        }
        private String fixture(String name) throws java.io.IOException {
            try (var input=new ClassPathResource("scraping/indeed/"+name+".html").getInputStream()) { return new String(input.readAllBytes(),StandardCharsets.UTF_8); }
        }
    }
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired FixtureCollector collector;
    @Autowired com.projects.job_tracker.application.observatory.CollectionControlService control;
    private static final String BASE="/api/observatory";
    @BeforeEach void resetFixture() { collector.mode="SUCCESS"; collector.calls.set(0); collector.suffix=UUID.randomUUID().toString().replace("-",""); collector.entered=null; collector.release=null; }
    private String send(String path,String payload) throws Exception {
        return mvc.perform(post(BASE+path).contentType(MediaType.APPLICATION_JSON).content(payload)).andExpect(status().is2xxSuccessful())
            .andReturn().getResponse().getContentAsString();
    }
    private String protocol(boolean automatic) throws Exception { return protocol(automatic,1); }
    private String protocol(boolean automatic,int pages) throws Exception {
        String segment=JsonPath.read(send("/segments","{\"spec\":{\"name\":\"Muestra\",\"roleFamily\":\"Backend\"},\"reason\":\"Prueba\"}"),"$.id");
        String spec=automatic ? """
            {"segmentId":"%s","segmentRevision":1,"name":"Portal","source":"indeed","query":"Java",
            "filters":"Ubicación: sin filtro; modalidad: sin filtro; antigüedad: sin filtro","intervalMinutes":10080,"resultLimit":10,
            "ordering":"Orden del portal","pagination":"Primera página","samplingMode":"EXPLORATORY","collectionPlan":{"platform":"INDEED"}}
            """.formatted(segment) : """
            {"segmentId":"%s","segmentRevision":1,"name":"Manual","source":"manual-%s","query":"Java","filters":"Sin filtros",
             "intervalMinutes":60,"resultLimit":10,"ordering":"Manual","pagination":"Manual","samplingMode":"EXPLORATORY"}
            """.formatted(segment,UUID.randomUUID());
        if (automatic && pages!=1) spec=spec.replace("Primera página","Hasta "+pages+" páginas").replace("\"platform\":\"INDEED\"","\"platform\":\"INDEED\",\"maxPages\":"+pages);
        return JsonPath.read(send("/protocols","{\"spec\":"+spec+",\"reason\":\"Prueba\"}"),"$.id");
    }
    private String run(String protocol) throws Exception { return JsonPath.read(send("/protocols/"+protocol+"/runs","{\"protocolRevision\":1,\"requestKey\":\""+UUID.randomUUID()+"\"}"),"$.id"); }
    @Test void multiPageProtocolRoundTripsAndOldProtocolsKeepTheirSinglePageLimit() throws Exception {
        String multi=protocol(true,3),legacy=protocol(true);
        mvc.perform(get(BASE+"/protocols/"+multi+"/history")).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].spec.collectionPlan.maxPages").value(3)).andExpect(jsonPath("$[0].spec.pagination").value("Hasta 3 páginas"));
        mvc.perform(get(BASE+"/protocols/"+legacy+"/history")).andExpect(jsonPath("$[0].spec.collectionPlan.maxPages").value(1));
    }
    @Test void laterPageFailureKeepsEarlierImportsPageHtmlAndTheActualVisitedCount() throws Exception {
        collector.mode="PAGE_FAILURE";
        String id=run(protocol(true,3));
        String result=send("/runs/"+id+"/collect","{}");
        assertThat((String)JsonPath.read(result,"$.state")).isEqualTo("PARTIAL");
        assertThat(((Number)JsonPath.read(result,"$.pagesVisited")).intValue()).isEqualTo(2);
        mvc.perform(get(BASE+"/runs/"+id+"/collection")).andExpect(jsonPath("$.pages.length()").value(2))
            .andExpect(jsonPath("$.pages[1].html").value("<html>Security check</html>"))
            .andExpect(jsonPath("$.pages[1].outcome").value("ACCESS_NOT_CONFIRMED"))
            .andExpect(jsonPath("$.items[0].status").value("IMPORTED"));
        send("/runs/"+id+"/collect","{}");
        mvc.perform(get(BASE+"/runs/"+id+"/collection")).andExpect(jsonPath("$.pages.length()").value(2));
    }
    @Test void collectsScopedEvidenceAndDoesNotClaimCompleteCoverageOrCreateTrackingJobs() throws Exception {
        long jobs=jdbc.queryForObject("SELECT count(*) FROM jobs",Long.class),apps=jdbc.queryForObject("SELECT count(*) FROM applications",Long.class);
        String protocol=protocol(true),run=run(protocol);
        mvc.perform(get(BASE+"/protocols/"+protocol+"/collection-preview").param("revision","1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.searchUrl").value("https://mx.indeed.com/jobs?q=Java"));
        String result=send("/runs/"+run+"/collect","{}"); assertThat((String)JsonPath.read(result,"$.state")).isEqualTo("PARTIAL");
        mvc.perform(get(BASE+"/runs/"+run+"/collection")).andExpect(jsonPath("$.attempt.status").value("FINISHED"))
            .andExpect(jsonPath("$.items[0].status").value("IMPORTED")).andExpect(jsonPath("$.items[0].listingHtml").isNotEmpty())
            .andExpect(jsonPath("$.items[0].detailHtml").isNotEmpty());
        String evidence=mvc.perform(get(BASE+"/runs/"+run+"/observations")).andReturn().getResponse().getContentAsString();
        String posting=JsonPath.read(evidence,"$[0].postingId");
        mvc.perform(get(BASE+"/postings/"+posting)).andExpect(jsonPath("$.observations.length()").value(1))
            .andExpect(jsonPath("$.revisions[0].method").value("AUTO_CAPTURE_V1"))
            .andExpect(jsonPath("$.revisions[0].data.quality.skillAnalysisReady").value(false))
            .andExpect(jsonPath("$.revisions[0].data.currency").isEmpty());
        send("/runs/"+run+"/collect","{}"); assertThat(collector.calls.get()).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM jobs",Long.class)).isEqualTo(jobs);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM applications",Long.class)).isEqualTo(apps);
    }
    @Test void rawEvidenceSurvivesInvalidParsedDataAndEmptyResultsAreNotMarketZero() throws Exception {
        collector.mode="INVALID"; String run=run(protocol(true));
        String result=send("/runs/"+run+"/collect","{}"); assertThat((String)JsonPath.read(result,"$.state")).isEqualTo("FAILED");
        mvc.perform(get(BASE+"/runs/"+run+"/collection")).andExpect(jsonPath("$.items[0].status").value("ERROR"))
            .andExpect(jsonPath("$.items[0].detailHtml").isNotEmpty());
        mvc.perform(get(BASE+"/runs/"+run+"/observations")).andExpect(jsonPath("$.length()").value(0));
        collector.mode="EMPTY"; String empty=run(protocol(true));
        assertThat((String)JsonPath.read(send("/runs/"+empty+"/collect","{}"),"$.state")).isEqualTo("FAILED");
    }
    @Test void interruptionAfterAnItemRetainsCommittedEvidenceAndReportsPartialCoverage() throws Exception {
        collector.mode="THROW"; String run=run(protocol(true));
        mvc.perform(post(BASE+"/runs/"+run+"/collect")).andExpect(status().isOk()).andExpect(jsonPath("$.state").value("PARTIAL"))
            .andExpect(jsonPath("$.pagesVisited").value(1)).andExpect(jsonPath("$.observationCount").value(1));
        mvc.perform(get(BASE+"/runs/"+run+"/collection")).andExpect(jsonPath("$.items[0].status").value("IMPORTED"));
    }
    @Test void automaticRepeatPreservesHumanInterpretationForUnchangedOriginal() throws Exception {
        String protocol=protocol(true),first=run(protocol); send("/runs/"+first+"/collect","{}");
        String events=mvc.perform(get(BASE+"/runs/"+first+"/observations")).andReturn().getResponse().getContentAsString();
        String posting=JsonPath.read(events,"$[0].postingId");
        mvc.perform(put(BASE+"/postings/"+posting).contentType(MediaType.APPLICATION_JSON).content("""
            {"expectedRevision":1,"reason":"Revisión humana","data":{"title":"Título corregido","requirementsReviewed":true,"skillRequirements":[]}}
            """ )).andExpect(status().isOk());
        String second=run(protocol); send("/runs/"+second+"/collect","{}");
        mvc.perform(get(BASE+"/postings/"+posting)).andExpect(jsonPath("$.observations.length()").value(2))
            .andExpect(jsonPath("$.revisions.length()").value(2)).andExpect(jsonPath("$.revisions[1].data.title").value("Título corregido"));
    }
    @Test void newManualItemIsAtomicIdempotentAndBelongsDirectlyToRun() throws Exception {
        String run=run(protocol(false)); String payload="""
            {"itemKey":"manual-1","externalId":"id-1","url":"https://example.com/new","originalText":"Java publicado",
             "observedAt":"%s","data":{"title":"Java"}}
            """.formatted(Instant.now());
        String item=send("/runs/"+run+"/items",payload),posting=JsonPath.read(item,"$.postingId"),observation=JsonPath.read(item,"$.id");
        assertThat((String)JsonPath.read(send("/runs/"+run+"/items",payload),"$.id")).isEqualTo(observation);
        mvc.perform(get(BASE+"/postings/"+posting)).andExpect(jsonPath("$.observations.length()").value(1))
            .andExpect(jsonPath("$.observations[0].runId").value(run)).andExpect(jsonPath("$.revisions[0].method").value("MANUAL_CAPTURE_V1"));
        mvc.perform(put(BASE+"/runs/"+run+"/finish").contentType(MediaType.APPLICATION_JSON).content("{\"state\":\"COMPLETE\",\"pagesVisited\":1,\"truncated\":false}"))
            .andExpect(status().isOk());
        assertThat((String)JsonPath.read(send("/runs/"+run+"/items",payload),"$.id")).isEqualTo(observation);
    }
    @Test void concurrentRequestsDoNotLaunchTwoCollectorsAndManualWritesAreBlockedDuringCollection() throws Exception {
        String run=run(protocol(true)); collector.entered=new CountDownLatch(1); collector.release=new CountDownLatch(1);
        try (var executor=Executors.newSingleThreadExecutor()) {
            var first=executor.submit(() -> { try { return send("/runs/"+run+"/collect","{}"); } catch(Exception ex) { throw new RuntimeException(ex); } });
            try {
                assertThat(collector.entered.await(5,TimeUnit.SECONDS)).isTrue();
                assertThat((String)JsonPath.read(send("/runs/"+run+"/collect","{}"),"$.state")).isEqualTo("RUNNING");
                mvc.perform(put(BASE+"/runs/"+run+"/finish").contentType(MediaType.APPLICATION_JSON).content("{\"state\":\"COMPLETE\",\"pagesVisited\":1,\"truncated\":false}"))
                    .andExpect(status().isBadRequest());
            } finally { collector.release.countDown(); }
            assertThat((String)JsonPath.read(first.get(10,TimeUnit.SECONDS),"$.state")).isEqualTo("PARTIAL");
            assertThat(collector.calls.get()).isEqualTo(1);
        }
    }
    @Test void interruptPreventsLateImportsButPreservesTheEvidenceReceivedAfterClosing() throws Exception {
        String run=run(protocol(true)); collector.entered=new CountDownLatch(1); collector.release=new CountDownLatch(1);
        try (var executor=Executors.newSingleThreadExecutor()) {
            var first=executor.submit(() -> { try { return send("/runs/"+run+"/collect","{}"); } catch(Exception ex) { throw new RuntimeException(ex); } });
            try {
                assertThat(collector.entered.await(5,TimeUnit.SECONDS)).isTrue();
                String closed=send("/runs/"+run+"/collection/interrupt","{\"reason\":\"Revisar sesión antes de continuar\"}");
                assertThat((String)JsonPath.read(closed,"$.state")).isEqualTo("FAILED");
            } finally { collector.release.countDown(); }
            assertThat((String)JsonPath.read(first.get(10,TimeUnit.SECONDS),"$.state")).isEqualTo("FAILED");
            mvc.perform(get(BASE+"/runs/"+run+"/observations")).andExpect(jsonPath("$.length()").value(0));
            mvc.perform(get(BASE+"/runs/"+run+"/collection")).andExpect(jsonPath("$.attempt.status").value("FINISHED"))
                .andExpect(jsonPath("$.items[0].status").value("ERROR")).andExpect(jsonPath("$.items[0].listingHtml").isNotEmpty());
        }
    }
    @Test void abandonedAttemptCanBeClosedWithoutLeavingPendingEvidenceOrStartingTheBrowserAgain() throws Exception {
        String run=run(protocol(true));
        assertThat(control.claim(UUID.fromString(run),"https://mx.indeed.com/jobs?q=Java","FIXTURE_V1")).isTrue();
        control.stage(UUID.fromString(run),new MarketCollector.Item("pending",Instant.now(),"<div>Evidencia pendiente</div>",null,false,"Evidencia pendiente",null,null),"FIXTURE_V1");
        send("/runs/"+run+"/collection/interrupt","{\"reason\":\"Cerrar intento abandonado después de reiniciar\"}");
        mvc.perform(get(BASE+"/runs/"+run+"/collection")).andExpect(jsonPath("$.items[0].status").value("ERROR"))
            .andExpect(jsonPath("$.items[0].listingHtml").value("<div>Evidencia pendiente</div>"));
        assertThat(collector.calls.get()).isZero();
    }
}

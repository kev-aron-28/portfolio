package com.projects.job_tracker.infrastructure.observatory;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import com.projects.job_tracker.domain.model.JobPlatform;
import com.projects.job_tracker.infrastructure.observatory.collection.SourceEvidenceParser;

class SourceEvidenceParserTest {
    private final SourceEvidenceParser parser=new SourceEvidenceParser();
    private String fixture(String platform,String name) throws Exception {
        try (var input=new ClassPathResource("scraping/"+platform+"/"+name+".html").getInputStream()) { return new String(input.readAllBytes(),StandardCharsets.UTF_8); }
    }
    @Test void preservesAllFourSourceFragmentsAndDoesNotInventIdentityForUnparseableCards() throws Exception {
        for (var source:JobPlatform.values()) {
            String listing=fixture(source.source(),"search-results");
            var item=parser.parse(source,0,listing,null,false,Instant.now());
            assertThat(item.listingHtml()).isEqualTo(listing);
            assertThat(item.parsed()).isNotNull(); assertThat(item.originalText()).contains(item.parsed().title());
            assertThat(item.detailVerified()).isFalse();
        }
        var invalid=parser.parse(JobPlatform.INDEED,0,"<div>Un cambio del portal</div>",null,false,Instant.now());
        assertThat(invalid.parsed()).isNull(); assertThat(invalid.originalText()).contains("cambio");
    }
    @Test void refusesWrongOccOrComputrabajoPanelAndPreservesItsRawHtmlForReview() throws Exception {
        String listing=Jsoup.parse(fixture("computrabajo","search-results")).select("article.box_offer").get(1).outerHtml(),detail=fixture("computrabajo","detail-panel");
        var matching=parser.parse(JobPlatform.COMPUTRABAJO,0,listing,detail,false,Instant.now());
        assertThat(matching.detailVerified()).isTrue();
        String wrong=detail.replace("D45DB2B366425F1B61373E686DCF3405","DIFFERENT");
        var mismatched=parser.parse(JobPlatform.COMPUTRABAJO,0,listing,wrong,false,Instant.now());
        assertThat(mismatched.detailHtml()).isEqualTo(wrong); assertThat(mismatched.detailVerified()).isFalse();
        assertThat(mismatched.originalText()).isEqualTo(Jsoup.parse(listing).text());
        String occ=fixture("occ","search-results"),occDetail=fixture("occ","detail-panel");
        assertThat(parser.parse(JobPlatform.OCC,0,occ,occDetail.replace("21239558","99999999"),false,Instant.now()).detailVerified()).isFalse();
    }
    @Test void includesStandaloneDetailOnlyAfterNavigationIdentityIsConfirmed() throws Exception {
        String listing=Jsoup.parse(fixture("indeed","search-results")).select("div.job_seen_beacon").stream().filter(e -> e.text().contains("Capgemini")).findFirst().orElseThrow().outerHtml(),detail=fixture("indeed","detail-panel");
        var unverified=parser.parse(JobPlatform.INDEED,0,listing,detail,false,Instant.now());
        assertThat(unverified.originalText()).isEqualTo(Jsoup.parse(listing).text());
        var verified=parser.parse(JobPlatform.INDEED,0,listing,detail,true,Instant.now());
        assertThat(verified.detailVerified()).isTrue(); assertThat(verified.originalText()).contains("microservicios");
        assertThat(verified.detailHtml()).isEqualTo(detail);
    }
}

package com.projects.job_tracker.domain.observatory;

import java.time.Instant;
import java.util.function.Consumer;
import com.projects.job_tracker.domain.model.ScrapedJob;

public interface MarketCollector {
    default String version() { return "OBSERVATORY_BROWSER_V1"; }
    String searchUrl(CaptureModels.ProtocolVersion protocol);
    Report collect(CaptureModels.ProtocolVersion protocol, Consumer<Item> sink);
    default Report collect(CaptureModels.ProtocolVersion protocol,Consumer<Item> sink,java.util.function.BooleanSupplier active) { return collect(protocol,sink); }
    default Report collect(CaptureModels.ProtocolVersion protocol,Consumer<Item> sink,java.util.function.BooleanSupplier active,Consumer<PageEvidence> pages) { return collect(protocol,sink,active); }
    record PageEvidence(int number,String url,Instant observedAt,String html,int cardCount,String fingerprint,String nextUrl,String outcome) {}
    record Item(String itemKey, Instant observedAt, String listingHtml, String detailHtml, boolean detailVerified,
            String originalText, ScrapedJob parsed, String issue) {
        public Item {
            itemKey=CaptureModels.required(itemKey,"Clave del elemento",200);
            if (observedAt==null) throw new IllegalArgumentException("La evidencia necesita fecha de observación");
            observedAt=observedAt.truncatedTo(java.time.temporal.ChronoUnit.MICROS);
        }
    }
    record Report(String searchUrl, String collectorVersion, int pagesVisited, boolean truncated, String notes) {}
}

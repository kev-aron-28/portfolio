package com.projects.job_tracker.infrastructure.observatory.collection;

import static org.assertj.core.api.Assertions.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import com.microsoft.playwright.*;
import com.projects.job_tracker.domain.model.*;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.port.ScrapingSettingsRepository;
import com.projects.job_tracker.infrastructure.scraping.*;
import org.jsoup.Jsoup;

/** Explicit local browser verification: all HTTP requests are fulfilled or aborted, never sent to portals. */
public final class PageBrowserVerification {
    public static void main(String[] args) throws Exception {
        var settings=new ScrapingSettings(0,10,List.of(JobPlatform.INDEED),null,250,null,250,null,250,null,250,false,1000);
        var holder=new ScrapingSettingsHolder(new ScrapingSettingsRepository() {
            public ScrapingSettings get() { return settings; }
            public ScrapingSettings save(ScrapingSettings ignored) { throw new UnsupportedOperationException(); }
        });
        var parser=new SourceEvidenceParser(); var collector=new BrowserMarketCollector(parser,holder,new RateLimiter(holder));
        String fixture=Files.readString(Path.of("src/test/resources/scraping/indeed/search-results.html"));
        String first=Jsoup.parse(fixture).select("div.job_seen_beacon").stream().filter(e->e.text().contains("Capgemini")).findFirst().orElseThrow().outerHtml();
        String id=parser.summary(JobPlatform.INDEED,first).orElseThrow().externalId(),second=first.replace(id,id+"next");
        try (var playwright=Playwright.create(); var browser=playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true)
                .setExecutablePath(Path.of(args[0])).setArgs(List.of("--no-sandbox")))) {
            for (String mode:List.of("END","REPEAT","OVERLAP","RESULT_LIMIT","PAGE_LIMIT","BLOCKED","SCOPE","INTERRUPT","NAV_FAIL","UNKNOWN","EMPTY")) {
                var protocol=protocol(JobPlatform.INDEED,mode.equals("PAGE_LIMIT") ? 1 : 3,mode.equals("RESULT_LIMIT") ? 1 : 10);
                String url=collector.searchUrl(protocol),next=url+"&start=10";
                var items=new ArrayList<MarketCollector.Item>(); var pages=new ArrayList<MarketCollector.PageEvidence>(); var active=new AtomicBoolean(true);
                try (var context=browser.newContext(); var page=context.newPage()) {
                    context.route("**/*",route->{
                        String target=route.request().url();
                        if (!target.equals(url) && !target.equals(next)) { route.fulfill(new Route.FulfillOptions().setContentType("text/html").setBody("<div id='jobDescriptionText'>Detalle local</div>")); return; }
                        boolean later=target.equals(next);
                        if (later && mode.equals("NAV_FAIL")) { route.abort(); return; }
                        String cards=later && !mode.equals("REPEAT") ? second : first;
                        if (later && mode.equals("OVERLAP")) cards=first+second;
                        String controls=later ? "<nav><button aria-label='Next page' disabled>Next</button></nav>" : "<a rel='next' href='"+next.replace("&","&amp;")+"'>Next</a>";
                        if (mode.equals("UNKNOWN")) controls="";
                        if (mode.equals("SCOPE")) controls="<a rel='next' href='?q=Python&amp;start=10'>Next</a>";
                        String html="<html><body>"+cards+controls+"</body></html>";
                        if (later && mode.equals("BLOCKED")) html="<html><title>Security check</title><body>Blocked</body></html>";
                        if (mode.equals("EMPTY")) html="<html><body>No cards</body></html>";
                        route.fulfill(new Route.FulfillOptions().setContentType("text/html").setBody(html));
                    });
                    var report=collector.collectPages(protocol,context,page,item->{items.add(item); if(mode.equals("INTERRUPT")) active.set(false);},active::get,pages::add);
                    switch(mode) {
                        case "END","OVERLAP" -> { assertThat(items).hasSize(2); assertThat(pages).hasSize(2); assertThat(report.notes()).contains("END_CONTROL_OBSERVED"); assertThat(report.truncated()).isFalse(); }
                        case "REPEAT" -> { assertThat(items).hasSize(1); assertThat(pages.getLast().outcome()).isEqualTo("REPEATED_PAGE"); }
                        case "RESULT_LIMIT" -> { assertThat(items).hasSize(1); assertThat(pages).hasSize(1); assertThat(report.truncated()).isTrue(); assertThat(report.notes()).contains("RESULT_LIMIT"); }
                        case "PAGE_LIMIT" -> { assertThat(pages).hasSize(1); assertThat(report.truncated()).isTrue(); assertThat(report.notes()).contains("PAGE_LIMIT"); }
                        case "BLOCKED" -> { assertThat(items).hasSize(1); assertThat(pages.getLast().html()).contains("Blocked"); assertThat(report.notes()).contains("ACCESS_NOT_CONFIRMED"); }
                        case "SCOPE" -> { assertThat(items).hasSize(1); assertThat(pages).hasSize(1); assertThat(report.notes()).contains("SEARCH_SCOPE_CHANGED"); }
                        case "INTERRUPT" -> { assertThat(items).hasSize(1); assertThat(pages).hasSize(1); assertThat(report.notes()).contains("INTERRUPTED"); }
                        case "NAV_FAIL" -> { assertThat(items).hasSize(1); assertThat(pages).hasSize(1); assertThat(report.notes()).contains("NAVIGATION_FAILED"); }
                        case "UNKNOWN" -> { assertThat(pages).hasSize(1); assertThat(report.notes()).contains("NAVIGATION_UNKNOWN"); }
                        case "EMPTY" -> { assertThat(items).isEmpty(); assertThat(pages).hasSize(1); assertThat(report.notes()).contains("NO_RECOGNIZED_CARDS"); }
                    }
                    assertThat(items.stream().map(MarketCollector.Item::itemKey).distinct().count()).isEqualTo(items.size());
                    System.out.println("PAGE_BROWSER_CASE_OK "+mode);
                }
            }
            for (var platform:JobPlatform.values()) {
                var protocol=protocol(platform,3,10); String url=collector.searchUrl(protocol);
                String cursor=switch(platform) { case LINKEDIN, INDEED -> "start"; case OCC -> "page"; case COMPUTRABAJO -> "p"; };
                String next=url+(url.contains("?") ? "&" : "?")+cursor+"=2";
                String card=switch(platform) { case LINKEDIN -> "<div class='base-card'>"; case INDEED -> "<div class='job_seen_beacon'>"; case OCC -> "<div class='card-job-offer'>"; case COMPUTRABAJO -> "<article class='box_offer' data-id='fixture'>"; };
                var pages=new ArrayList<MarketCollector.PageEvidence>();
                try (var context=browser.newContext(); var page=context.newPage()) {
                    context.route("**/*",route->{ boolean later=route.request().url().equals(next);
                        route.fulfill(new Route.FulfillOptions().setContentType("text/html").setBody("<html>"+card+(later ? "Second card" : "First card")+(platform==JobPlatform.COMPUTRABAJO ? "</article>" : "</div>")+
                            (later ? "<button rel='next' disabled>Next</button>" : "<a rel='next' href='"+next.replace("&","&amp;")+"'>Next</a>")+"</html>")); });
                    var report=collector.collectPages(protocol,context,page,item->{},()->true,pages::add);
                    assertThat(report.pagesVisited()).as(platform.name()).isEqualTo(2); assertThat(pages.getLast().outcome()).isEqualTo("END_CONTROL_OBSERVED");
                    System.out.println("PAGE_BROWSER_SOURCE_OK "+platform);
                }
            }
        }
        System.out.println("OBSERVATORY_PAGINATION_BROWSER_OK");
    }
    private static CaptureModels.ProtocolVersion protocol(JobPlatform platform,int pages,int limit) {
        var plan=new CollectionPlan(platform,null,null,platform==JobPlatform.LINKEDIN ? 7 : null,pages);
        var spec=new CaptureModels.ProtocolSpec(UUID.randomUUID(),1,"Fixture",platform.source(),"Java",plan.filterDescription(),60,limit,"Orden del portal",plan.paginationDescription(),CaptureModels.SamplingMode.EXPLORATORY,plan);
        return new CaptureModels.ProtocolVersion(UUID.randomUUID(),1,spec,"Local browser verification",Instant.now());
    }
}

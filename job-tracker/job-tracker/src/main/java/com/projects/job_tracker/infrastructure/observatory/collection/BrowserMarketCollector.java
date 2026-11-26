package com.projects.job_tracker.infrastructure.observatory.collection;

import java.net.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import com.projects.job_tracker.domain.model.*;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.infrastructure.scraping.*;
import com.projects.job_tracker.infrastructure.scraping.linkedin.LinkedInSearchUrlBuilder;
import com.projects.job_tracker.infrastructure.scraping.indeed.IndeedSearchUrlBuilder;
import com.projects.job_tracker.infrastructure.scraping.occ.OccSearchUrlBuilder;
import com.projects.job_tracker.infrastructure.scraping.computrabajo.ComputrabajoSearchUrlBuilder;

@Component
public class BrowserMarketCollector implements MarketCollector {
    public static final String VERSION="OBSERVATORY_BROWSER_V2";
    private final SourceEvidenceParser parser;
    private final ScrapingSettingsHolder settings;
    private final RateLimiter limiter;
    public BrowserMarketCollector(SourceEvidenceParser parser,ScrapingSettingsHolder settings,RateLimiter limiter) { this.parser=parser; this.settings=settings; this.limiter=limiter; }
    public String version() { return VERSION; }
    public String searchUrl(CaptureModels.ProtocolVersion protocol) {
        CollectionPlan plan=protocol.spec().collectionPlan();
        if (plan==null) throw new IllegalArgumentException("El protocolo no tiene un plan ejecutable; guarda una nueva versión con portal y filtros");
        String mode=plan.workMode()==null ? null : switch(plan.workMode()) { case REMOTE -> "remote"; case HYBRID -> "hybrid"; case ONSITE -> "onsite"; };
        var criteria=new ScrapeCriteria(protocol.spec().query(),new ScrapeFilterSet(plan.location(),null,null,null,mode,plan.postedWithinDays()),protocol.spec().resultLimit());
        return switch(plan.platform()) { case LINKEDIN -> LinkedInSearchUrlBuilder.build(criteria); case INDEED -> IndeedSearchUrlBuilder.build(criteria); case OCC -> OccSearchUrlBuilder.build(criteria); case COMPUTRABAJO -> ComputrabajoSearchUrlBuilder.build(criteria); };
    }
    public Report collect(CaptureModels.ProtocolVersion protocol,Consumer<Item> sink) { return collect(protocol,sink,()->true); }
    public Report collect(CaptureModels.ProtocolVersion protocol,Consumer<Item> sink,java.util.function.BooleanSupplier active) {
        return collect(protocol,sink,active,page->{});
    }
    public synchronized Report collect(CaptureModels.ProtocolVersion protocol,Consumer<Item> sink,java.util.function.BooleanSupplier active,Consumer<PageEvidence> pages) {
        String url=searchUrl(protocol); var platform=protocol.spec().collectionPlan().platform();
        if (!active.getAsBoolean()) return new Report(url,VERSION,0,false,"Captura interrumpida antes de consultar el portal");
        try (Playwright playwright=Playwright.create(); Browser browser=playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {
            var options=new Browser.NewContextOptions().setLocale("es-MX");
            String session=session(platform); if (session!=null && !session.isBlank()) options.setStorageStatePath(Path.of(session));
            try (BrowserContext context=browser.newContext(options); Page page=context.newPage()) {
                return collectPages(protocol,context,page,sink,active,pages);
            }
        }
    }
    // Shared by the production browser and local, intercepted-HTTP browser verification.
    Report collectPages(CaptureModels.ProtocolVersion protocol,BrowserContext context,Page page,Consumer<Item> sink,java.util.function.BooleanSupplier active,Consumer<PageEvidence> pages) {
        String url=searchUrl(protocol),target=url; var platform=protocol.spec().collectionPlan().platform();
        int timeout=timeout(platform),visited=0,emitted=0;
        Set<String> seenUrls=new HashSet<>(),seenPages=new HashSet<>(),seenItems=new HashSet<>();
        var policy=new PaginationPolicy();
        page.setDefaultTimeout(timeout);
        for (int number=1;number<=protocol.spec().collectionPlan().maxPages();number++) {
            if (!active.getAsBoolean()) return new Report(url,VERSION,visited,false,"INTERRUPTED: captura interrumpida");
            if (!seenUrls.add(target)) return new Report(url,VERSION,visited,false,"REPEATED_URL: enlace ya visitado");
            limiter.acquire();
            if (!active.getAsBoolean()) return new Report(url,VERSION,visited,false,"INTERRUPTED: captura interrumpida antes de navegar");
            Response response;
            try { response=page.navigate(target,new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED)); }
            catch (RuntimeException ex) {
                return new Report(url,VERSION,visited,false,"NAVIGATION_FAILED: no se confirmó la siguiente página; evidencia anterior conservada");
            }
            visited++;
            boolean denied=response==null || response.status()>=400 || blocked(page);
            boolean scopeChanged=!policy.sameSearch(platform,url,page.url());
            String[] selectors=cardSelectors(platform); Locator cards=page.locator(selectors[0]);
            if (!denied && !scopeChanged) try { page.waitForSelector(String.join(",",selectors),new Page.WaitForSelectorOptions().setTimeout(timeout)); } catch (TimeoutError ex) { /* never infer zero demand */ }
            for (String selector:selectors) if (page.locator(selector).count()>0) { cards=page.locator(selector); break; }
            int count=cards.count();
            List<String> listings=new ArrayList<>(),identities=new ArrayList<>();
            for (int i=0;i<count;i++) {
                String html=(String)cards.nth(i).evaluate("e => e.outerHTML"); listings.add(html);
                String identity="html:"+EvidenceHash.sha256(html);
                try { identity=parser.summary(platform,html).map(j -> j.externalId()!=null ? "id:"+j.externalId() : "url:"+SourceIdentityKey.url(j.url())).orElse(identity); }
                catch (RuntimeException ex) { /* malformed cards still belong in page evidence */ }
                identities.add(identity);
            }
            String fingerprint=EvidenceHash.sha256(String.join("\n",identities.stream().sorted().toList()));
            boolean repeated=!seenPages.add(fingerprint);
            String html=page.content(); var next=policy.inspect(platform,url,page.url(),html);
            String outcome=denied ? "ACCESS_NOT_CONFIRMED" : scopeChanged ? "SEARCH_SCOPE_CHANGED" : count==0 ? "NO_RECOGNIZED_CARDS" : repeated ? "REPEATED_PAGE" : next.outcome();
            if (!denied && !scopeChanged && count>0 && !repeated) {
                long novel=identities.stream().distinct().filter(id->!seenItems.contains(id)).count();
                if (novel>=protocol.spec().resultLimit()-emitted) outcome="RESULT_LIMIT";
                else if (number==protocol.spec().collectionPlan().maxPages() && !next.outcome().equals("END_CONTROL_OBSERVED")) outcome="PAGE_LIMIT";
            }
            pages.accept(new PageEvidence(number,page.url(),Instant.now(),html,count,fingerprint,next.url(),outcome));
            if (denied || scopeChanged || count==0 || repeated) return new Report(url,VERSION,visited,false,outcome+": cobertura no confirmada");
            for (int i=0;i<count && emitted<protocol.spec().resultLimit();i++) {
                if (!active.getAsBoolean()) return new Report(url,VERSION,visited,false,"INTERRUPTED: evidencia conservada");
                    Locator card=cards.nth(i); String listing=listings.get(i);
                    String identity=identities.get(i);
                    if (!seenItems.add(identity)) continue;
                    emitted++;
                    String detail=null; boolean verified=false; String extractionIssue=null;
                    try {
                    var summary=parser.summary(platform,listing);
                    if (summary.isPresent()) {
                        limiter.acquire();
                        if (platform==JobPlatform.LINKEDIN || platform==JobPlatform.INDEED) {
                            if (allowedDetail(platform,summary.get().url())) try (Page detailPage=context.newPage()) {
                                detailPage.setDefaultTimeout(timeout);
                                try {
                                    Response detailResponse=detailPage.navigate(summary.get().url(),new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                                    if (detailResponse!=null && detailResponse.status()<400 && !blocked(detailPage)) {
                                        detailPage.waitForSelector(detailSelector(platform));
                                        detail=(String)detailPage.locator(detailSelector(platform)).first().evaluate("e => e.outerHTML");
                                        verified=matchesNavigation(summary.get(),detailPage.url());
                                    }
                                } catch (RuntimeException ex) { /* preserve listing even if detail fails */ }
                            }
                        } else {
                            try {
                                card.scrollIntoViewIfNeeded(); card.click(); page.waitForSelector(detailSelector(platform));
                                if (platform==JobPlatform.COMPUTRABAJO) page.waitForFunction("id => Array.from(document.querySelectorAll('[data-offers-grid-detail-container][data-oi]')).some(e=>e.getAttribute('data-oi')===id)",summary.get().externalId());
                                else page.waitForFunction("id => Array.from(document.querySelectorAll('[data-offer-content] [data-url]')).some(e=>{try{return new URL(e.getAttribute('data-url'),location.href).pathname.split('/').filter(Boolean).pop()===id}catch(_){return false}})",summary.get().externalId());
                            } catch (RuntimeException ex) { /* identity will be verified from captured panel before enrichment */ }
                            if (page.locator(detailSelector(platform)).count()>0) detail=(String)page.locator(detailSelector(platform)).first().evaluate("e => e.outerHTML");
                        }
                    }
                    } catch (RuntimeException ex) { extractionIssue="Error al extraer el elemento: "+ex.getClass().getSimpleName(); }
                    sink.accept(extractionIssue==null ? parser.parse(platform,(number-1)*10000+i,listing,detail,verified,Instant.now()) : parser.failed((number-1)*10000+i,listing,detail,Instant.now(),extractionIssue));

            }
            if (emitted>=protocol.spec().resultLimit()) return new Report(url,VERSION,visited,true,"RESULT_LIMIT: límite de elementos alcanzado");
            if (number==protocol.spec().collectionPlan().maxPages()) return new Report(url,VERSION,visited,!next.outcome().equals("END_CONTROL_OBSERVED"),outcome+": límite o fin de navegación observado; muestreo exploratorio");
            if (next.url()==null) return new Report(url,VERSION,visited,false,next.outcome()+": no se confirmó continuidad; cobertura del mercado desconocida");
            target=next.url();
        }
        throw new IllegalStateException("Límite de paginación inválido");
    }
    private String[] cardSelectors(JobPlatform platform) { return switch(platform) {
        case LINKEDIN -> new String[]{"li[data-occludable-job-id]","div.base-card"};
        case INDEED -> new String[]{"#mosaic-jobResults div.job_seen_beacon","div.job_seen_beacon"};
        case OCC -> new String[]{".card-job-offer","div[data-offer-id]"};
        case COMPUTRABAJO -> new String[]{"article.box_offer[data-offers-grid-offer-item-container]","article.box_offer[data-id]"};
    }; }
    private String detailSelector(JobPlatform platform) { return switch(platform) {
        case LINKEDIN -> ".description__text, .show-more-less-html__markup";
        case INDEED -> ".jobsearch-RightPane, .jobsearch-JobComponent, #jobDescriptionText";
        case OCC -> "[data-offer-content]";
        case COMPUTRABAJO -> "[data-offers-grid-detail-container]";
    }; }
    private boolean blocked(Page page) {
        String title=page.title().toLowerCase(Locale.ROOT),url=page.url().toLowerCase(Locale.ROOT);
        return title.contains("captcha") || title.contains("security check") || title.contains("just a moment") || url.contains("/login") || url.contains("/authwall");
    }
    private boolean allowedDetail(JobPlatform platform,String url) {
        String host=URI.create(url).getHost(); if (host==null) return false; host=host.toLowerCase(Locale.ROOT);
        String domain=platform==JobPlatform.LINKEDIN ? "linkedin.com" : "indeed.com";
        return host.equals(domain) || host.endsWith("."+domain);
    }
    private boolean matchesNavigation(ScrapedJob job,String actual) {
        if (!allowedDetail(job.platform(),actual)) return false;
        if (SourceIdentityKey.url(job.url()).equals(SourceIdentityKey.url(actual))) return true;
        if (job.platform()==JobPlatform.LINKEDIN && job.externalId()!=null && job.externalId().matches("[0-9]+"))
            return URI.create(actual).getPath().matches(".*/jobs/view/(?:[^/]*-)?"+job.externalId()+"/?");
        String query=URI.create(actual).getRawQuery();
        if (job.platform()==JobPlatform.INDEED && query!=null) for (String part:query.split("&")) {
            String[] entry=part.split("=",2); if (entry.length==2 && entry[0].equals("jk") && entry[1].equals(job.externalId())) return true;
        }
        return false;
    }
    private String session(JobPlatform platform) { return switch(platform) { case LINKEDIN -> settings.linkedinStorageStatePath(); case INDEED -> settings.indeedStorageStatePath(); case OCC -> settings.occStorageStatePath(); case COMPUTRABAJO -> settings.computrabajoStorageStatePath(); }; }
    private int timeout(JobPlatform platform) { return switch(platform) { case LINKEDIN -> settings.linkedinPageTimeoutMs(); case INDEED -> settings.indeedPageTimeoutMs(); case OCC -> settings.occPageTimeoutMs(); case COMPUTRABAJO -> settings.computrabajoPageTimeoutMs(); }; }
}

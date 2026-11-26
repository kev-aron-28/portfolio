package com.projects.job_tracker.infrastructure.observatory.collection;

import java.time.Instant;
import java.util.*;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import com.projects.job_tracker.domain.model.*;
import com.projects.job_tracker.domain.observatory.MarketCollector;
import com.projects.job_tracker.infrastructure.scraping.linkedin.LinkedInJobParser;
import com.projects.job_tracker.infrastructure.scraping.indeed.IndeedJobParser;
import com.projects.job_tracker.infrastructure.scraping.occ.OccJobParser;
import com.projects.job_tracker.infrastructure.scraping.computrabajo.ComputrabajoJobParser;

@Component
public class SourceEvidenceParser {
    private final LinkedInJobParser linkedin=new LinkedInJobParser();
    private final IndeedJobParser indeed=new IndeedJobParser();
    private final OccJobParser occ=new OccJobParser();
    private final ComputrabajoJobParser computrabajo=new ComputrabajoJobParser();
    public Optional<ScrapedJob> summary(JobPlatform platform,String html) {
        return (switch(platform) {
            case LINKEDIN -> linkedin.parse(html,1); case INDEED -> indeed.parse(html,1);
            case OCC -> occ.parse(html,1); case COMPUTRABAJO -> computrabajo.parse(html,1);
        }).stream().findFirst();
    }
    public MarketCollector.Item parse(JobPlatform platform,int ordinal,String listing,String detail,boolean navigationVerified,Instant at) {
        try { return parseInternal(platform,ordinal,listing,detail,navigationVerified,at); }
        catch (RuntimeException ex) { return failed(ordinal,listing,detail,at,"Error de extracción: "+ex.getClass().getSimpleName()); }
    }
    public MarketCollector.Item failed(int ordinal,String listing,String detail,Instant at,String issue) {
        return new MarketCollector.Item("card-"+ordinal,at,listing,detail,false,Jsoup.parse(listing).text(),null,issue);
    }
    private MarketCollector.Item parseInternal(JobPlatform platform,int ordinal,String listing,String detail,boolean navigationVerified,Instant at) {
        var summary=summary(platform,listing);
        ScrapedJob parsed=summary.orElse(null); boolean verified=false;
        if (parsed!=null && detail!=null && !detail.isBlank()) {
            if (platform==JobPlatform.OCC) {
                var extracted=occ.parseDetailPanel(detail);
                verified=extracted.isPresent() && Objects.equals(extracted.get().externalId(),parsed.externalId());
                if (verified) parsed=copyDetail(parsed,extracted.get());
            } else if (platform==JobPlatform.COMPUTRABAJO) {
                var id=Jsoup.parse(detail).selectFirst("[data-oi]");
                verified=id!=null && id.attr("data-oi").equals(parsed.externalId());
                if (verified) parsed=computrabajo.enrichWithDetail(parsed,detail).orElse(parsed);
            } else {
                verified=navigationVerified;
                if (verified) parsed=(platform==JobPlatform.LINKEDIN ? linkedin.enrichWithDetail(parsed,detail) : indeed.enrichWithDetail(parsed,detail)).orElse(parsed);
            }
        }
        String text=Jsoup.parse(listing).text();
        if (verified) text+="\n"+Jsoup.parse(detail).text();
        String issue=parsed==null ? "Tarjeta sin anuncio identificable" : !verified ? "Sólo tarjeta: el detalle no pudo verificarse" : null;
        return new MarketCollector.Item("card-"+ordinal,at,listing,detail,verified,text,parsed,issue);
    }
    private ScrapedJob copyDetail(ScrapedJob summary,ScrapedJob detail) {
        return new ScrapedJob(summary.platform(),summary.externalId(),detail.title(),prefer(detail.companyName(),summary.companyName()),prefer(detail.description(),summary.description()),prefer(detail.location(),summary.location()),
            detail.salaryMin()!=null ? detail.salaryMin() : summary.salaryMin(),detail.salaryMax()!=null ? detail.salaryMax() : summary.salaryMax(),summary.url(),summary.postedAt(),detail.employmentType(),detail.workMode(),detail.category(),detail.subcategory(),detail.benefits(),detail.requirements());
    }
    private String prefer(String detail,String summary) { return detail==null ? summary : detail; }
}

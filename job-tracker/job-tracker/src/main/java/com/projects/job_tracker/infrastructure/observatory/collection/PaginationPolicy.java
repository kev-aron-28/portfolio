package com.projects.job_tracker.infrastructure.observatory.collection;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.jsoup.Jsoup;
import com.projects.job_tracker.domain.model.JobPlatform;

/** Follows observed links only; missing controls never prove exhaustion. */
public final class PaginationPolicy {
    public record Next(String url,String outcome) {}
    public Next inspect(JobPlatform platform,String searchUrl,String currentUrl,String html) {
        var doc=Jsoup.parse(html,currentUrl);
        String scope=switch(platform) {
            case INDEED -> "[data-testid=pagination-page-next], nav [aria-label], a[rel=next], button[rel=next]";
            case LINKEDIN -> ".artdeco-pagination button, .artdeco-pagination a, a[rel=next], button[rel=next]";
            case OCC -> "[class*=pagination] a, [class*=pagination] button, a[rel=next], button[rel=next]";
            case COMPUTRABAJO -> ".bRS a, [class*=pagination] a, [class*=pagination] button, a[rel=next], button[rel=next]";
        };
        var candidates=doc.select(scope).stream().filter(e -> e.attr("rel").equalsIgnoreCase("next") ||
            e.attr("data-testid").equals("pagination-page-next") ||
            (e.attr("aria-label")+" "+e.text()).toLowerCase(Locale.ROOT).matches(".*\\b(next|siguiente)\\b.*")).toList();
        if (candidates.size()!=1) return new Next(null,"NAVIGATION_UNKNOWN");
        var next=candidates.getFirst();
        if (next.hasAttr("disabled") || next.attr("aria-disabled").equalsIgnoreCase("true") || next.hasClass("disabled") ||
                next.parent()!=null && (next.parent().hasClass("disabled") || next.parent().attr("aria-disabled").equalsIgnoreCase("true")))
            return new Next(null,"END_CONTROL_OBSERVED");
        String href=next.attr("href");
        if (href.isBlank() || href.startsWith("#")) return new Next(null,"UNSUPPORTED_NAVIGATION");
        try {
            String target=href.startsWith("?") ? currentUrl.split("[?#]",2)[0]+href : URI.create(currentUrl).resolve(href).toString();
            if (!sameSearch(platform,searchUrl,target)) return new Next(null,"SEARCH_SCOPE_CHANGED");
            return new Next(target,"NEXT_LINK_OBSERVED");
        } catch (IllegalArgumentException ex) { return new Next(null,"UNSAFE_NAVIGATION"); }
    }
    public boolean sameSearch(JobPlatform platform,String original,String candidate) {
        try {
            URI a=URI.create(original),b=URI.create(candidate);
            return Objects.equals(a.getScheme(),b.getScheme()) && Objects.equals(a.getHost(),b.getHost()) && a.getPort()==b.getPort() &&
                b.getUserInfo()==null && b.getFragment()==null && Objects.equals(a.getPath(),b.getPath()) && query(a,platform).equals(query(b,platform));
        } catch (IllegalArgumentException ex) { return false; }
    }
    private Map<String,List<String>> query(URI uri,JobPlatform platform) {
        String cursor=switch(platform) { case INDEED, LINKEDIN -> "start"; case OCC -> "page"; case COMPUTRABAJO -> "p"; };
        Map<String,List<String>> values=new TreeMap<>();
        if (uri.getRawQuery()!=null) for (String part:uri.getRawQuery().split("&")) {
            String[] pair=part.split("=",2);
            String key=URLDecoder.decode(pair[0],StandardCharsets.UTF_8),value=pair.length==1 ? "" : URLDecoder.decode(pair[1],StandardCharsets.UTF_8);
            if (key.equals(cursor)) {
                if (!value.matches("[0-9]{1,8}")) throw new IllegalArgumentException("Cursor inválido");
            } else values.computeIfAbsent(key,k->new ArrayList<>()).add(value);
        }
        values.values().forEach(Collections::sort);
        return values;
    }
}

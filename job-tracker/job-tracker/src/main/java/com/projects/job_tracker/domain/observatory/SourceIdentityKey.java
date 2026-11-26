package com.projects.job_tracker.domain.observatory;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Conservative locator normalization. Does not equate platforms, HTTP/HTTPS, paths or job IDs. */
public final class SourceIdentityKey {
    private SourceIdentityKey() {}
    public static String source(String source) { return source.strip().toLowerCase(Locale.ROOT); }
    public static String externalId(String externalId) {
        if (externalId == null || externalId.isBlank()) return null;
        if (externalId.strip().length() > 300) throw new IllegalArgumentException("El identificador externo admite hasta 300 caracteres");
        return externalId.strip();
    }
    public static String url(String value) {
        if (value == null || value.isBlank()) return null;
        URI uri = URI.create(value.strip());
        if (uri.getHost() == null || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())))
            throw new IllegalArgumentException("La URL debe ser HTTP o HTTPS");
        String scheme = uri.getScheme().toLowerCase(Locale.ROOT), host = uri.getHost().toLowerCase(Locale.ROOT);
        int port = uri.getPort();
        if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) port = -1;
        String query = uri.getRawQuery();
        if (query != null) {
            var retained = new ArrayList<String>();
            for (String parameter : query.split("&", -1)) {
                String key = URLDecoder.decode(parameter.split("=",2)[0], StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
                if (!key.startsWith("utm_") && !Set.of("gclid","fbclid","msclkid").contains(key)) retained.add(parameter);
            }
            query = retained.isEmpty() ? null : String.join("&",retained);
        }
        String path = uri.getRawPath();
        if (path == null || path.isEmpty()) path = "/";
        String authority = (uri.getRawUserInfo() == null ? "" : uri.getRawUserInfo()+"@") + host + (port < 0 ? "" : ":"+port);
        return scheme+"://"+authority+path+(query == null || query.isEmpty() ? "" : "?"+query)+(uri.getRawFragment() == null ? "" : "#"+uri.getRawFragment());
    }
}

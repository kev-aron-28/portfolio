package com.projects.http;

import java.util.HashMap;
import java.util.Map;

public class ServerResponse {
    private final int statusCode;
    private final String reasonPhrase;
    private final Map<String, String> headers;
    private final String body;

    public ServerResponse(int statusCode, String reasonPhrase, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ServerResponse ok(String body) {
        return builder()
            .status(200, "OK")
            .body(body)
            .build();
    }
    
    public static ServerResponse notFound(String body) {
        return builder()
            .status(404, "Bad request")
            .body(body)
            .build();
    }

    public static class Builder {
        private int statusCode;
        private String reasonPhrase;
        private final Map<String, String> headers = new HashMap<>();
        private String body;

        public Builder status(int statusCode, String reasonPhrase) {
            this.statusCode = statusCode;
            this.reasonPhrase = reasonPhrase;
            return this;
        }

        public Builder header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public ServerResponse build() {
            return new ServerResponse(statusCode, reasonPhrase, headers, body);
        }
    }   
}

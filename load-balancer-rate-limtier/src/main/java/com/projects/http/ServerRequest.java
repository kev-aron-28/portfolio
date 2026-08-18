package com.projects.http;

import java.util.Map;

public class ServerRequest {
    private final String method;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> headers;
    private String body;
    
    public ServerRequest(String method, String path, String httpVersion, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public String toString() {
        return "ServerRequest [method=" + method + ", path=" + path + ", httpVersion=" + httpVersion + ", headers="
                + headers + ", body=" + body + "]";
    } 

    
}

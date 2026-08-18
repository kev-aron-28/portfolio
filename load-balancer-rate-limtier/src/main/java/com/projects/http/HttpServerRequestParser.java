package com.projects.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServerRequestParser {
    public ServerRequest parse(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                socket.getInputStream(),
                StandardCharsets.UTF_8
            )
        );

        String requestLine = reader.readLine();

        if(requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Invalid HTTP request");
        }

        String[] requestParts = requestLine.split(" ");

        if (requestParts.length != 3) {
            throw new IOException("Invalid HTTP request line");
        }

        String method = requestParts[0];
        String path = requestParts[1];
        String httpVersion = requestParts[2];

        Map<String, String> headers = new HashMap<>();
    
        String line;
        while((line = reader.readLine()) != null) {
            if(line.isEmpty()) break;

            String header[] = line.split(":", 2);

            if(header.length != 2) {
                throw new IOException("Invalid HTTP header");
            }

            String name = header[0].trim();
            String value = header[1].trim();

            headers.put(name, value);
        } 

        String body = "";

        String contentLength = headers.get("Content-Length");

        if(contentLength != null) {
            int len = Integer.parseInt(contentLength);

            char bodyChars[] = new char[len];

            int read = reader.read(bodyChars);

            if (read != len) {
                throw new IOException("Incomplete HTTP body");
            }

            body = new String(bodyChars);
        }

        return new ServerRequest(method, path, httpVersion, headers, body);
    }
}

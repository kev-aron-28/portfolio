package com.projects.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServerResponseParser {

    public static ServerResponse parse(Socket backendSocket) throws IOException {
        InputStream input = backendSocket.getInputStream();

        // Status line
        String statusLine = readLine(input);

        if (statusLine == null || statusLine.isEmpty()) {
            throw new IOException("Invalid HTTP response");
        }

        String[] responseParts = statusLine.split(" ", 3);

        if (responseParts.length != 3) {
            throw new IOException("Invalid HTTP status line");
        }

        String httpVersion = responseParts[0];

        int statusCode;

        try {
            statusCode = Integer.parseInt(responseParts[1]);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid HTTP status code", e);
        }

        String reasonPhrase = responseParts[2];

        Map<String, String> headers = new HashMap<>();

        String line;

        while ((line = readLine(input)) != null) {

            if (line.isEmpty()) {
                break;
            }

            String[] header = line.split(":", 2);

            if (header.length != 2) {
                throw new IOException("Invalid HTTP header");
            }

            String name = header[0].trim();
            String value = header[1].trim();

            headers.put(name, value);
        }

        String body = "";

        String contentLength = headers.get("Content-Length");

        if (contentLength != null) {

            int length;

            try {
                length = Integer.parseInt(contentLength);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid Content-Length", e);
            }

            byte[] bodyBytes = input.readNBytes(length);

            if (bodyBytes.length != length) {
                throw new IOException("Incomplete HTTP body");
            }

            body = new String(
                    bodyBytes,
                    StandardCharsets.UTF_8
            );
        }

        return new ServerResponse(
                statusCode,
                reasonPhrase,
                headers,
                body
        );
    }

    private static String readLine(InputStream input) throws IOException {

        StringBuilder line = new StringBuilder();

        int current;

        while ((current = input.read()) != -1) {

            if (current == '\r') {

                int next = input.read();

                if (next == '\n') {
                    break;
                }

            } else {
                line.append((char) current);
            }
        }

        if (current == -1 && line.isEmpty()) {
            return null;
        }

        return line.toString();
    }
}

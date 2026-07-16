package com.projects.message_worker.domain.services;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateRenderer {
    private static final Pattern PATTERN = Pattern.compile("\\{\\{(.*?)}}");

    public String render(String template, Map<String, Object> payload) {
        Matcher matcher = PATTERN.matcher(template);

        StringBuffer result = new StringBuffer();
    
        while(matcher.find()) {
            String variable = matcher.group(1).trim();

            Object value = payload.get(variable);

            matcher.appendReplacement(result, value.toString());
        }

        matcher.appendTail(result);

        return result.toString();
    }
}

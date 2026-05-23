package com.projects.api_service.domain.services;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class TemplateVariableExtractor {
    private static final Pattern PATTERN = Pattern.compile("\\{\\{(.*?)}}");

    public Set<String> extract(String template) {
        Matcher matcher = PATTERN.matcher(template);

        Set<String> variables = new HashSet<>();

        while(matcher.find()) {
            variables.add(matcher.group(1).trim());
        }
        
        return variables;
    }
}

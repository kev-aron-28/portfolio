package com.projects.api_service.domain;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import com.projects.api_service.domain.errors.InvalidArgument;
import com.projects.api_service.domain.errors.InvalidTemplate;

public final class Template {
    private Long id;

    private String name;

    private NotificationChannel channel;

    private String subject;

    private String content;

    private LocalDateTime createdAt;

    public Template(
            String name,
            NotificationChannel channel,
            String subject,
            String content
    ) {
        if(subject == null || content == null || name == null) {
            throw new InvalidArgument();
        }

        this.validateTemplate(content);

        this.name = name;
        this.channel = channel;
        this.subject = subject;
        this.content = content;
    }

    public void validateTemplate(String content) {
        Pattern templatePattern = Pattern.compile(".*\\{[a-zA-Z_][a-zA-Z0-9_]*\\}.*");

        if (!templatePattern.matcher(content).matches()) {
            throw new InvalidTemplate();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
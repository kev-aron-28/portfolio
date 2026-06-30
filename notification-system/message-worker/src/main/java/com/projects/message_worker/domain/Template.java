package com.projects.message_worker.domain;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import com.projects.message_worker.domain.errors.InvalidArgument;
import com.projects.message_worker.domain.errors.InvalidTemplate;

public final class Template implements Serializable {
    private Long id;

    private String name;

    private NotificationChannel channel;

    private String subject;

    private String content;

    private LocalDateTime createdAt;
    private final static Pattern templatePattern = Pattern.compile(".*\\{\\{\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*\\}\\}.*");

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
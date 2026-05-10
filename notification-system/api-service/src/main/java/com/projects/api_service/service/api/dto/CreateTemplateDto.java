package com.projects.api_service.service.api.dto;

import com.projects.api_service.domain.NotificationChannel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTemplateDto(
    @NotEmpty
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    String name,

    @NotNull(message = "Channel is required")
    NotificationChannel channel,

    @NotBlank
    @Size(max = 255, message = "Subject cannot exceed 255 characters")
    String subject,

    @NotBlank
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    String content
) {}

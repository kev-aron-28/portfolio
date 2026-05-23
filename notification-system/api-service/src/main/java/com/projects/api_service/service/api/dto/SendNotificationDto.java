package com.projects.api_service.service.api.dto;

import java.util.Map;

import com.projects.api_service.domain.NotificationChannel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SendNotificationDto(
    @NotNull
    NotificationChannel channel,

    @NotBlank
    String recipient,

    @NotNull
    @Positive
    Long templateId,

    @NotNull
    Map<String, Object> payload
) {}

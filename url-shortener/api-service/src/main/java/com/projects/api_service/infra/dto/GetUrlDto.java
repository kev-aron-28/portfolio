package com.projects.api_service.infra.dto;

import jakarta.validation.constraints.NotEmpty;

public record GetUrlDto(
    @NotEmpty
    String shortUrl
) {}

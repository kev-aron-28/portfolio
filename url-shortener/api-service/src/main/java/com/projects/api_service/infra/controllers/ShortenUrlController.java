package com.projects.api_service.infra.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.api_service.application.ShortenUrlUseCase;
import com.projects.api_service.domain.Url;
import com.projects.api_service.infra.ApiResponse;
import com.projects.api_service.infra.dto.ShortenUrlDto;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1")
public class ShortenUrlController {
    private final ShortenUrlUseCase shorten;

    public ShortenUrlController(ShortenUrlUseCase shorten) {
        this.shorten = shorten;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ApiResponse<Url>> shortenUrl(@RequestBody @Valid ShortenUrlDto entity) {
        Url shortenedUrl = this.shorten.run(entity);

        return ResponseEntity.ok().body(
            ApiResponse.success(shortenedUrl)
        );
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable("shortUrl") String shortUrl) {

        return ResponseEntity.ok().build();
    }
}

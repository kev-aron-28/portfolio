package com.projects.api_service.infra.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.api_service.application.GetLongUrlUseCase;
import com.projects.api_service.application.ShortenUrlUseCase;
import com.projects.api_service.domain.Url;
import com.projects.api_service.infra.ApiResponse;
import com.projects.api_service.infra.dto.ShortenUrlDto;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1")
public class ShortenUrlController {
    private final ShortenUrlUseCase shorten;
    private final GetLongUrlUseCase getLong;

    public ShortenUrlController(ShortenUrlUseCase shorten, GetLongUrlUseCase getLong) {
        this.shorten = shorten;
        this.getLong = getLong;
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
        System.out.println("[PARAM" + shortUrl + "]");
        
        Url result = this.getLong.run(shortUrl);

        String longUrl = result.getLongUrl();

        if(!longUrl.startsWith("http://") && !longUrl.startsWith("https://")) {
            longUrl = "https://" + longUrl;
        }

        return ResponseEntity
            .status(302)
            .location(URI.create(longUrl))
            .build();
    }
}

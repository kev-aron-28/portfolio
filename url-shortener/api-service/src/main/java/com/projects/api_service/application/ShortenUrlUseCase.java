package com.projects.api_service.application;

import com.projects.api_service.domain.Shortener;
import com.projects.api_service.domain.Url;
import com.projects.api_service.infra.dto.ShortenUrlDto;
import com.projects.api_service.infra.repo.JpaUrlRepository;

public class ShortenUrlUseCase {
    private final Shortener shortener;
    private final JpaUrlRepository repository;

    public ShortenUrlUseCase(Shortener shortener, JpaUrlRepository repository) {
        this.shortener = shortener;
        this.repository = repository;
    }

    public Url run(ShortenUrlDto url) throws Exception {
        int attempts = 0;

        StringBuilder longUrl = new StringBuilder(url.longUrl());

        boolean shortUrlFound = false;

        while (!shortUrlFound) { 
            String attempt = shortener.hash(longUrl.toString());
        }

        return new Url(url.longUrl(), "");
    }

    

}

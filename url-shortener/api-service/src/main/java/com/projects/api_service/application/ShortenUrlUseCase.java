package com.projects.api_service.application;

import org.springframework.stereotype.Service;

import com.projects.api_service.domain.Shortener;
import com.projects.api_service.domain.Url;
import com.projects.api_service.infra.dto.ShortenUrlDto;
import com.projects.api_service.infra.entities.UrlEntity;
import com.projects.api_service.infra.repo.JpaUrlRepository;

@Service
public class ShortenUrlUseCase {
    private final Shortener shortener;
    private final JpaUrlRepository repository;

    public ShortenUrlUseCase(Shortener shortener, JpaUrlRepository repository) {
        this.shortener = shortener;
        this.repository = repository;
    }

    public Url run(ShortenUrlDto url) {
        int attempts = 1;
        
        StringBuilder longUrl = new StringBuilder(url.longUrl());
        String shortUrl;
        
        while (true) { 
            String attempt = shortener.hash(longUrl.toString());
            
            try {
                repository.save(new UrlEntity(attempt, url.longUrl()));
                shortUrl = attempt;
                break;
            } catch (Exception e) {
                longUrl.append(attempts++);
            }
        }

        return new Url(url.longUrl(), shortUrl);
    }
}

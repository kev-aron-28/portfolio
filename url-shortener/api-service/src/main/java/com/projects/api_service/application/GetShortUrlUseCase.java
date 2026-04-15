package com.projects.api_service.application;

import org.springframework.stereotype.Service;

import com.projects.api_service.domain.Url;
import com.projects.api_service.domain.exceptions.ShortUrlNotFound;
import com.projects.api_service.infra.dto.GetUrlDto;
import com.projects.api_service.infra.entities.UrlEntity;
import com.projects.api_service.infra.repo.JpaUrlRepository;

@Service
public class GetShortUrlUseCase {
    private final JpaUrlRepository repository;

    public GetShortUrlUseCase(JpaUrlRepository repository) {
        this.repository = repository;
    }

    public Url run (GetUrlDto url) {
        UrlEntity entity = this.repository.findByShortUrl(url.shortUrl())
                                    .orElseThrow(() -> new ShortUrlNotFound());
        
        Url result = new Url(entity.getLongUrl(), entity.getShortUrl());
        
        return result;
    }
}

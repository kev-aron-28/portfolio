package com.projects.api_service.application;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.projects.api_service.domain.Url;
import com.projects.api_service.domain.exceptions.ShortUrlNotFound;
import com.projects.api_service.infra.entities.UrlEntity;
import com.projects.api_service.infra.repo.JpaUrlRepository;


@Service
public class GetLongUrlUseCase {
    private final JpaUrlRepository repository;
    private final RedisTemplate<String, String> redisTemplate;

    public GetLongUrlUseCase(JpaUrlRepository repository, RedisTemplate<String, String> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    public Url run (String shortUrl) {

        String cached = redisTemplate.opsForValue().get(shortUrl);

        if(cached != null) {
            return new Url(cached, shortUrl);
        }

        UrlEntity entity = this.repository.findByShortUrl(shortUrl)
                                    .orElseThrow(() -> new ShortUrlNotFound());

        redisTemplate.opsForValue().set(entity.getShortUrl(), entity.getLongUrl(), Duration.ofHours(24));
        
        Url result = new Url(entity.getLongUrl(), entity.getShortUrl());
        
        return result;
    }
}

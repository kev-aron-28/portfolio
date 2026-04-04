package com.projects.api_service.infra.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.api_service.infra.dto.ShortenUrlDto;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1")
public class ShortenUrlController {
    
    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody @Valid ShortenUrlDto entity) {
        //TODO: process POST request
        
        return "";
    }
    
}

package com.projects.api_service.service.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.api_service.application.CreateTemplate;
import com.projects.api_service.service.api.dto.CreateTemplateDto;
import com.projects.api_service.service.api.response.ApiResponse;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {
    
    private final CreateTemplate templateCreator;

    public TemplateController(CreateTemplate templateCreator) {
        this.templateCreator = templateCreator;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<String>> createTemplate(@Valid @RequestBody CreateTemplateDto entity) {
        this.templateCreator.run(entity);

        return ResponseEntity.ok().body(
            ApiResponse.success("Template created", null)
        ); 
    }
    
}

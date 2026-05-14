package com.projects.api_service.service.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.api_service.service.api.dto.CreateTemplateDto;
import com.projects.api_service.service.api.response.ApiResponse;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {
    
    @PostMapping()
    public ResponseEntity<ApiResponse<String>> postMethodName(@Valid @RequestBody CreateTemplateDto entity) {
        return ResponseEntity.ok().body(
            ApiResponse.success("Template created", null)
        );
    }
    
}

package com.portfolio.backend.infra.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/features")
public class FeatureFlagController {
    
    @GetMapping()
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello world");
    }
}

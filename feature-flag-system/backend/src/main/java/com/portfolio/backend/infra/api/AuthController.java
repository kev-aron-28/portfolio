package com.portfolio.backend.infra.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @GetMapping()
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
}

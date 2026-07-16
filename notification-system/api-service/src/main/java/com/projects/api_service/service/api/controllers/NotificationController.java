package com.projects.api_service.service.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.api_service.application.SendNotification;
import com.projects.api_service.service.api.dto.SendNotificationDto;
import com.projects.api_service.service.api.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/send")
public class NotificationController {
    
    private final SendNotification send;

    public NotificationController(SendNotification send) {
        this.send = send;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<String>> postMethodName(@RequestBody @Valid SendNotificationDto entity) {
        this.send.publish(entity);

        return ResponseEntity.ok(
            ApiResponse.success("Notification sent", null)
        );
    }
    
}

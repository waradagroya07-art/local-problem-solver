package com.localproblemsolver.controller;

import com.localproblemsolver.dto.NotificationResponse;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public NotificationResponse createNotification(
            @RequestParam String message,
            @RequestParam NotificationType type,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return notificationService.createNotification(
                message,
                type,
                userEmail
        );
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationResponse> getMyNotifications(
            Authentication authentication) {

        String userEmail = authentication.getName();

        return notificationService.getMyNotifications(userEmail);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public NotificationResponse getNotification(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return notificationService.getNotification(
                id,
                userEmail
        );
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public NotificationResponse markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return notificationService.markAsRead(
                id,
                userEmail
        );
    }
}
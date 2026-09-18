package com.localproblemsolver.controller;

import com.localproblemsolver.dto.NotificationResponse;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.service.NotificationService;
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
    public NotificationResponse createNotification(
            @RequestParam String message,
            @RequestParam NotificationType type) {

        return notificationService.createNotification(
                message,
                type
        );
    }

    @GetMapping
    public List<NotificationResponse> getAllNotifications() {

        return notificationService.getAllNotifications();
    }

    @GetMapping("/{id}")
    public NotificationResponse getNotification(
            @PathVariable Long id) {

        return notificationService.getNotification(id);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markAsRead(
            @PathVariable Long id) {

        return notificationService.markAsRead(id);
    }
}
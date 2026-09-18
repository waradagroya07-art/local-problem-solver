package com.localproblemsolver.service;

import com.localproblemsolver.dto.NotificationResponse;
import com.localproblemsolver.entity.Notification;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    public NotificationResponse createNotification(
            String message,
            NotificationType type) {

        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Notification message is required"
            );
        }

        if (type == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Notification type is required"
            );
        }

        Notification notification = new Notification();

        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification =
                notificationRepository.save(notification);

        return convertToResponse(savedNotification);
    }

    public List<NotificationResponse> getAllNotifications() {

        return notificationRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public NotificationResponse getNotification(Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Notification not found with id: " + id
                                )
                        );

        return convertToResponse(notification);
    }

    public NotificationResponse markAsRead(Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Notification not found with id: " + id
                                )
                        );

        notification.setRead(true);

        Notification updatedNotification =
                notificationRepository.save(notification);

        return convertToResponse(updatedNotification);
    }

    private NotificationResponse convertToResponse(
            Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
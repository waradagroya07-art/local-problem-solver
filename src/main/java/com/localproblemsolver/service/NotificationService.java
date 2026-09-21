package com.localproblemsolver.service;

import com.localproblemsolver.dto.NotificationResponse;
import com.localproblemsolver.entity.Notification;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.NotificationRepository;
import com.localproblemsolver.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public NotificationResponse createNotification(
            String message,
            NotificationType type,
            String userEmail) {

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

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with email: " + userEmail
                        )
                );

        Notification notification = new Notification();

        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUser(user);

        Notification savedNotification =
                notificationRepository.save(notification);

        return convertToResponse(savedNotification);
    }

    public List<NotificationResponse> getMyNotifications(
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with email: " + userEmail
                        )
                );

        return notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public NotificationResponse getNotification(
            Long id,
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with email: " + userEmail
                        )
                );

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Notification not found with id: " + id
                                )
                        );

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this notification"
            );
        }

        return convertToResponse(notification);
    }

    public NotificationResponse markAsRead(
            Long id,
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with email: " + userEmail
                        )
                );

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Notification not found with id: " + id
                                )
                        );

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to modify this notification"
            );
        }

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
                notification.getCreatedAt(),
                notification.getUser().getId()
        );
    }
}
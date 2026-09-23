package com.localproblemsolver.controller;

import com.localproblemsolver.dto.NotificationResponse;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    private final NotificationService service =
            mock(NotificationService.class);
    private final NotificationController controller =
            new NotificationController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void createNotification_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("superadmin@gmail.com");

        NotificationType type =
                NotificationType.values()[0];

        NotificationResponse expected =
                mock(NotificationResponse.class);

        when(service.createNotification(
                "Test notification",
                type,
                "superadmin@gmail.com"))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.createNotification(
                        "Test notification",
                        type,
                        authentication)
        );

        verify(service).createNotification(
                "Test notification",
                type,
                "superadmin@gmail.com"
        );
    }

    @Test
    void getMyNotifications_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        List<NotificationResponse> expected =
                List.of(mock(NotificationResponse.class));

        when(service.getMyNotifications(
                "citizen@gmail.com"))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getMyNotifications(
                        authentication)
        );

        verify(service).getMyNotifications(
                "citizen@gmail.com");
    }

    @Test
    void getNotification_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        NotificationResponse expected =
                mock(NotificationResponse.class);

        when(service.getNotification(
                10L,
                "citizen@gmail.com"))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getNotification(
                        10L,
                        authentication)
        );

        verify(service).getNotification(
                10L,
                "citizen@gmail.com"
        );
    }

    @Test
    void markAsRead_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        NotificationResponse expected =
                mock(NotificationResponse.class);

        when(service.markAsRead(
                10L,
                "citizen@gmail.com"))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.markAsRead(
                        10L,
                        authentication)
        );

        verify(service).markAsRead(
                10L,
                "citizen@gmail.com"
        );
    }
}

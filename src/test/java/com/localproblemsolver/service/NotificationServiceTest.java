package com.localproblemsolver.service;

import com.localproblemsolver.dto.NotificationResponse;
import com.localproblemsolver.entity.Notification;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.NotificationRepository;
import com.localproblemsolver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Citizen A");
        user.setEmail("citizen@gmail.com");
        user.setRole(Role.CITIZEN);

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Citizen B");
        anotherUser.setEmail("citizenb@gmail.com");
        anotherUser.setRole(Role.CITIZEN);
    }

    // =========================================================
    // CREATE NOTIFICATION - SUCCESS
    // =========================================================

    @Test
    void createNotificationSuccessfully() {

        LocalDateTime createdAt = LocalDateTime.now();

        Notification savedNotification = new Notification();
        savedNotification.setId(100L);
        savedNotification.setMessage("Your problem was validated.");
        savedNotification.setType(NotificationType.PROBLEM_CREATED);
        savedNotification.setRead(false);
        savedNotification.setCreatedAt(createdAt);
        savedNotification.setUser(user);

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(savedNotification);

        NotificationResponse result =
                notificationService.createNotification(
                        "Your problem was validated.",
                        NotificationType.PROBLEM_CREATED,
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(
                "Your problem was validated.",
                result.getMessage()
        );
        assertEquals(
                NotificationType.PROBLEM_CREATED,
                result.getType()
        );
        assertFalse(result.isRead());
        assertEquals(1L, result.getUserId());

        verify(notificationRepository)
                .save(any(Notification.class));
    }

    // =========================================================
    // CREATE - BLANK MESSAGE
    // =========================================================

    @Test
    void createNotificationFailsWhenMessageIsNull() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.createNotification(
                                null,
                                NotificationType.PROBLEM_CREATED,
                                "citizen@gmail.com"
                        )
                );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );

        verify(userRepository, never())
                .findByEmail(anyString());

        verify(notificationRepository, never())
                .save(any());
    }

    @Test
    void createNotificationFailsWhenMessageIsBlank() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.createNotification(
                                "   ",
                                NotificationType.PROBLEM_CREATED,
                                "citizen@gmail.com"
                        )
                );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );

        verify(userRepository, never())
                .findByEmail(anyString());

        verify(notificationRepository, never())
                .save(any());
    }

    // =========================================================
    // CREATE - NULL TYPE
    // =========================================================

    @Test
    void createNotificationFailsWhenTypeIsNull() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.createNotification(
                                "Test notification",
                                null,
                                "citizen@gmail.com"
                        )
                );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );

        verify(userRepository, never())
                .findByEmail(anyString());

        verify(notificationRepository, never())
                .save(any());
    }

    // =========================================================
    // CREATE - USER NOT FOUND
    // =========================================================

    @Test
    void createNotificationFailsWhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.createNotification(
                                "Test notification",
                                NotificationType.PROBLEM_CREATED,
                                "unknown@gmail.com"
                        )
                );

        assertEquals(
                404,
                exception.getStatusCode().value()
        );

        verify(notificationRepository, never())
                .save(any());
    }

    // =========================================================
    // CREATE - VERIFY ENTITY
    // =========================================================

    @Test
    void createNotificationSetsCorrectEntityValues() {

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        NotificationResponse result =
                notificationService.createNotification(
                        "Assignment received.",
                        NotificationType.PROBLEM_ASSIGNED,
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertEquals(
                "Assignment received.",
                result.getMessage()
        );
        assertEquals(
                NotificationType.PROBLEM_ASSIGNED,
                result.getType()
        );
        assertFalse(result.isRead());
        assertEquals(1L, result.getUserId());
        assertNotNull(result.getCreatedAt());

        verify(notificationRepository)
                .save(argThat(notification ->
                        notification.getMessage()
                                .equals("Assignment received.")
                                && notification.getType()
                                == NotificationType.PROBLEM_ASSIGNED
                                && !notification.isRead()
                                && notification.getUser()
                                .equals(user)
                                && notification.getCreatedAt()
                                != null
                ));
    }

    // =========================================================
    // GET MY NOTIFICATIONS
    // =========================================================

    @Test
    void getMyNotificationsReturnsUserNotifications() {

        Notification first = createNotification(
                1L,
                "First notification",
                NotificationType.PROBLEM_CREATED,
                false,
                user
        );

        Notification second = createNotification(
                2L,
                "Second notification",
                NotificationType.COMMENT_ADDED,
                true,
                user
        );

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(first, second));

        List<NotificationResponse> result =
                notificationService.getMyNotifications(
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(
                "First notification",
                result.get(0).getMessage()
        );

        assertEquals(
                "Second notification",
                result.get(1).getMessage()
        );

        verify(notificationRepository)
                .findAllByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMyNotificationsReturnsEmptyListWhenNoNotifications() {

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of());

        List<NotificationResponse> result =
                notificationService.getMyNotifications(
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMyNotificationsFailsWhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService
                                .getMyNotifications(
                                        "unknown@gmail.com"
                                )
                );

        assertEquals(
                404,
                exception.getStatusCode().value()
        );

        verify(notificationRepository, never())
                .findAllByUserIdOrderByCreatedAtDesc(anyLong());
    }

    // =========================================================
    // GET SINGLE NOTIFICATION
    // =========================================================

    @Test
    void getNotificationSuccessfully() {

        Notification notification = createNotification(
                100L,
                "Problem assigned to authority.",
                NotificationType.PROBLEM_ASSIGNED,
                false,
                user
        );

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(100L))
                .thenReturn(Optional.of(notification));

        NotificationResponse result =
                notificationService.getNotification(
                        100L,
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(
                "Problem assigned to authority.",
                result.getMessage()
        );
        assertEquals(
                NotificationType.PROBLEM_ASSIGNED,
                result.getType()
        );
        assertFalse(result.isRead());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void getNotificationFailsWhenNotificationDoesNotExist() {

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.getNotification(
                                999L,
                                "citizen@gmail.com"
                        )
                );

        assertEquals(
                404,
                exception.getStatusCode().value()
        );
    }

    @Test
    void userCannotAccessAnotherUsersNotification() {

        Notification notification = createNotification(
                101L,
                "Private notification",
                NotificationType.PROBLEM_CREATED,
                false,
                anotherUser
        );

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(101L))
                .thenReturn(Optional.of(notification));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.getNotification(
                                101L,
                                "citizen@gmail.com"
                        )
                );

        assertEquals(
                403,
                exception.getStatusCode().value()
        );
    }

    @Test
    void getNotificationFailsWhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.getNotification(
                                100L,
                                "unknown@gmail.com"
                        )
                );

        assertEquals(
                404,
                exception.getStatusCode().value()
        );

        verify(notificationRepository, never())
                .findById(anyLong());
    }

    // =========================================================
    // MARK AS READ
    // =========================================================

    @Test
    void userCanMarkOwnNotificationAsRead() {

        Notification notification = createNotification(
                200L,
                "Unread notification",
                NotificationType.COMMENT_ADDED,
                false,
                user
        );

        Notification updatedNotification = createNotification(
                200L,
                "Unread notification",
                NotificationType.COMMENT_ADDED,
                true,
                user
        );

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(200L))
                .thenReturn(Optional.of(notification));

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(updatedNotification);

        NotificationResponse result =
                notificationService.markAsRead(
                        200L,
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertTrue(result.isRead());

        assertTrue(notification.isRead());

        verify(notificationRepository)
                .save(notification);
    }

    @Test
    void userCannotMarkAnotherUsersNotificationAsRead() {

        Notification notification = createNotification(
                201L,
                "Private notification",
                NotificationType.PROBLEM_CREATED,
                false,
                anotherUser
        );

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(201L))
                .thenReturn(Optional.of(notification));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.markAsRead(
                                201L,
                                "citizen@gmail.com"
                        )
                );

        assertEquals(
                403,
                exception.getStatusCode().value()
        );

        verify(notificationRepository, never())
                .save(any());
    }

    @Test
    void markAsReadFailsWhenNotificationDoesNotExist() {

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.markAsRead(
                                999L,
                                "citizen@gmail.com"
                        )
                );

        assertEquals(
                404,
                exception.getStatusCode().value()
        );

        verify(notificationRepository, never())
                .save(any());
    }

    @Test
    void markAsReadFailsWhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> notificationService.markAsRead(
                                100L,
                                "unknown@gmail.com"
                        )
                );

        assertEquals(
                404,
                exception.getStatusCode().value()
        );

        verify(notificationRepository, never())
                .findById(anyLong());

        verify(notificationRepository, never())
                .save(any());
    }

    @Test
    void markingNotificationAsReadPersistsReadState() {

        Notification notification = createNotification(
                300L,
                "Test notification",
                NotificationType.PROBLEM_CREATED,
                false,
                user
        );

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(300L))
                .thenReturn(Optional.of(notification));

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        NotificationResponse result =
                notificationService.markAsRead(
                        300L,
                        "citizen@gmail.com"
                );

        assertTrue(result.isRead());

        verify(notificationRepository)
                .save(argThat(saved ->
                        saved.getId().equals(300L)
                                && saved.isRead()
                ));
    }

    // =========================================================
    // RESPONSE MAPPING
    // =========================================================

    @Test
    void responseContainsCorrectNotificationData() {

        LocalDateTime createdAt = LocalDateTime.now();

        Notification notification = new Notification();
        notification.setId(400L);
        notification.setMessage("SLA breached.");
        notification.setType(NotificationType.SLA_BREACHED);
        notification.setRead(true);
        notification.setCreatedAt(createdAt);
        notification.setUser(user);

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(400L))
                .thenReturn(Optional.of(notification));

        NotificationResponse result =
                notificationService.getNotification(
                        400L,
                        "citizen@gmail.com"
                );

        assertEquals(400L, result.getId());
        assertEquals(
                "SLA breached.",
                result.getMessage()
        );
        assertEquals(
                NotificationType.SLA_BREACHED,
                result.getType()
        );
        assertTrue(result.isRead());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(1L, result.getUserId());
    }

    // =========================================================
    // HELPER
    // =========================================================

    private Notification createNotification(
            Long id,
            String message,
            NotificationType type,
            boolean read,
            User notificationUser) {

        Notification notification = new Notification();

        notification.setId(id);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(read);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUser(notificationUser);

        return notification;
    }
}
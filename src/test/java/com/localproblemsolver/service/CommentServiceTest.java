package com.localproblemsolver.service;

import com.localproblemsolver.dto.CommentRequest;
import com.localproblemsolver.dto.CommentResponse;
import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.Comment;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.CommentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @InjectMocks
    private CommentService commentService;

    private User citizen;
    private User anotherCitizen;
    private User moderator;
    private User superAdmin;
    private User authorityUser;
    private User anotherAuthorityUser;

    private Problem citizenProblem;
    private Problem anotherProblem;

    private com.localproblemsolver.entity.Authority authority;
    private com.localproblemsolver.entity.Authority anotherAuthority;

    @BeforeEach
    void setUp() {

        // -------------------------
        // USERS
        // -------------------------

        citizen = new User();
        citizen.setId(1L);
        citizen.setName("Citizen A");
        citizen.setEmail("citizen@gmail.com");
        citizen.setRole(Role.CITIZEN);

        anotherCitizen = new User();
        anotherCitizen.setId(2L);
        anotherCitizen.setName("Citizen B");
        anotherCitizen.setEmail("citizenb@gmail.com");
        anotherCitizen.setRole(Role.CITIZEN);

        moderator = new User();
        moderator.setId(3L);
        moderator.setName("Moderator");
        moderator.setEmail("moderator@gmail.com");
        moderator.setRole(Role.MODERATOR);

        superAdmin = new User();
        superAdmin.setId(4L);
        superAdmin.setName("Super Admin");
        superAdmin.setEmail("admin@gmail.com");
        superAdmin.setRole(Role.SUPER_ADMIN);

        // -------------------------
        // AUTHORITIES
        // -------------------------

        authority = new com.localproblemsolver.entity.Authority();
        authority.setId(10L);
        authority.setName("Road Authority");

        anotherAuthority = new com.localproblemsolver.entity.Authority();
        anotherAuthority.setId(20L);
        anotherAuthority.setName("Water Authority");

        authorityUser = new User();
        authorityUser.setId(5L);
        authorityUser.setName("Authority User");
        authorityUser.setEmail("authority@gmail.com");
        authorityUser.setRole(Role.AUTHORITY);
        authorityUser.setAuthority(authority);

        anotherAuthorityUser = new User();
        anotherAuthorityUser.setId(6L);
        anotherAuthorityUser.setName("Another Authority");
        anotherAuthorityUser.setEmail("anotherauthority@gmail.com");
        anotherAuthorityUser.setRole(Role.AUTHORITY);
        anotherAuthorityUser.setAuthority(anotherAuthority);

        // -------------------------
        // PROBLEMS
        // -------------------------

        citizenProblem = new Problem();
        citizenProblem.setId(1L);
        citizenProblem.setTitle("Pothole on Main Road");
        citizenProblem.setUser(citizen);

        anotherProblem = new Problem();
        anotherProblem.setId(2L);
        anotherProblem.setTitle("Garbage Issue");
        anotherProblem.setUser(anotherCitizen);
    }

    // =========================================================
    // ADD COMMENT - CITIZEN
    // =========================================================

    @Test
    void citizenCanAddCommentToOwnProblem() {

        CommentRequest request = new CommentRequest();
        request.setText("The pothole is getting bigger.");

        Comment savedComment = new Comment();
        savedComment.setId(100L);
        savedComment.setProblem(citizenProblem);
        savedComment.setText(request.getText());
        savedComment.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(savedComment);

        CommentResponse result = commentService.addComment(
                1L,
                request,
                "citizen@gmail.com"
        );

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1L, result.getProblemId());
        assertEquals("The pothole is getting bigger.", result.getText());

        verify(commentRepository).save(any(Comment.class));

        // Owner should NOT receive notification for own comment
        verify(notificationService, never()).createNotification(
                anyString(),
                any(NotificationType.class),
                anyString()
        );
    }

    @Test
    void citizenCannotAddCommentToAnotherCitizensProblem() {

        CommentRequest request = new CommentRequest();
        request.setText("Unauthorized comment");

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.addComment(
                        2L,
                        request,
                        "citizen@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(commentRepository, never()).save(any());
    }

    // =========================================================
    // ADD COMMENT - MODERATOR
    // =========================================================

    @Test
    void moderatorCanAddCommentToAnyProblem() {

        CommentRequest request = new CommentRequest();
        request.setText("Moderator review comment");

        Comment savedComment = new Comment();
        savedComment.setId(101L);
        savedComment.setProblem(anotherProblem);
        savedComment.setText(request.getText());
        savedComment.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(savedComment);

        CommentResponse result = commentService.addComment(
                2L,
                request,
                "moderator@gmail.com"
        );

        assertNotNull(result);
        assertEquals(101L, result.getId());

        verify(commentRepository).save(any(Comment.class));

        // Problem owner should be notified
        verify(notificationService).createNotification(
                eq("A new comment was added to your problem #2."),
                eq(NotificationType.COMMENT_ADDED),
                eq("citizenb@gmail.com")
        );
    }

    // =========================================================
    // ADD COMMENT - SUPER ADMIN
    // =========================================================

    @Test
    void superAdminCanAddCommentToAnyProblem() {

        CommentRequest request = new CommentRequest();
        request.setText("Admin comment");

        Comment savedComment = new Comment();
        savedComment.setId(102L);
        savedComment.setProblem(anotherProblem);
        savedComment.setText(request.getText());
        savedComment.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(superAdmin));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(savedComment);

        CommentResponse result = commentService.addComment(
                2L,
                request,
                "admin@gmail.com"
        );

        assertNotNull(result);
        assertEquals(102L, result.getId());

        verify(commentRepository).save(any(Comment.class));

        verify(notificationService).createNotification(
                eq("A new comment was added to your problem #2."),
                eq(NotificationType.COMMENT_ADDED),
                eq("citizenb@gmail.com")
        );
    }

    // =========================================================
    // ADD COMMENT - AUTHORITY
    // =========================================================

    @Test
    void assignedAuthorityCanAddComment() {

        CommentRequest request = new CommentRequest();
        request.setText("Authority investigation update");

        Assignment assignment = new Assignment();
        assignment.setProblem(anotherProblem);
        assignment.setAuthority(authority);

        Comment savedComment = new Comment();
        savedComment.setId(103L);
        savedComment.setProblem(anotherProblem);
        savedComment.setText(request.getText());
        savedComment.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.findByProblemId(2L))
                .thenReturn(Optional.of(assignment));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(savedComment);

        CommentResponse result = commentService.addComment(
                2L,
                request,
                "authority@gmail.com"
        );

        assertNotNull(result);
        assertEquals(103L, result.getId());

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void authorityCannotCommentOnAnotherAuthoritiesProblem() {

        Assignment assignment = new Assignment();
        assignment.setProblem(anotherProblem);
        assignment.setAuthority(authority);

        CommentRequest request = new CommentRequest();
        request.setText("Unauthorized authority comment");

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("anotherauthority@gmail.com"))
                .thenReturn(Optional.of(anotherAuthorityUser));

        when(assignmentRepository.findByProblemId(2L))
                .thenReturn(Optional.of(assignment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.addComment(
                        2L,
                        request,
                        "anotherauthority@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(commentRepository, never()).save(any());
    }

    @Test
    void authorityWithoutLinkedAuthorityCannotComment() {

        User authorityWithoutLink = new User();
        authorityWithoutLink.setId(7L);
        authorityWithoutLink.setEmail("unlinked@gmail.com");
        authorityWithoutLink.setRole(Role.AUTHORITY);
        authorityWithoutLink.setAuthority(null);

        CommentRequest request = new CommentRequest();
        request.setText("Test comment");

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("unlinked@gmail.com"))
                .thenReturn(Optional.of(authorityWithoutLink));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.addComment(
                        2L,
                        request,
                        "unlinked@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(commentRepository, never()).save(any());
    }

    @Test
    void authorityCannotCommentWhenProblemIsNotAssigned() {

        CommentRequest request = new CommentRequest();
        request.setText("Authority comment");

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.findByProblemId(2L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.addComment(
                        2L,
                        request,
                        "authority@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(commentRepository, never()).save(any());
    }

    // =========================================================
    // GET COMMENTS - CITIZEN
    // =========================================================

    @Test
    void citizenCanGetCommentsForOwnProblem() {

        Comment comment = new Comment();
        comment.setId(200L);
        comment.setProblem(citizenProblem);
        comment.setText("Existing comment");

        LocalDateTime createdAt = LocalDateTime.now();
        comment.setCreatedAt(createdAt);

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(commentRepository.findByProblemId(1L))
                .thenReturn(List.of(comment));

        List<CommentResponse> result =
                commentService.getComments(
                        1L,
                        "citizen@gmail.com"
                );

        assertEquals(1, result.size());
        assertEquals(200L, result.get(0).getId());
        assertEquals(1L, result.get(0).getProblemId());
        assertEquals("Existing comment", result.get(0).getText());
        assertEquals(createdAt, result.get(0).getCreatedAt());
    }

    @Test
    void citizenCannotGetCommentsForAnotherCitizensProblem() {

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.getComments(
                        2L,
                        "citizen@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(commentRepository, never()).findByProblemId(anyLong());
    }

    // =========================================================
    // GET COMMENTS - MODERATOR
    // =========================================================

    @Test
    void moderatorCanGetCommentsForAnyProblem() {

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(commentRepository.findByProblemId(2L))
                .thenReturn(List.of());

        List<CommentResponse> result =
                commentService.getComments(
                        2L,
                        "moderator@gmail.com"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(commentRepository).findByProblemId(2L);
    }

    // =========================================================
    // GET COMMENTS - SUPER ADMIN
    // =========================================================

    @Test
    void superAdminCanGetCommentsForAnyProblem() {

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(superAdmin));

        when(commentRepository.findByProblemId(2L))
                .thenReturn(List.of());

        List<CommentResponse> result =
                commentService.getComments(
                        2L,
                        "admin@gmail.com"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(commentRepository).findByProblemId(2L);
    }

    // =========================================================
    // GET COMMENTS - AUTHORITY
    // =========================================================

    @Test
    void assignedAuthorityCanGetComments() {

        Assignment assignment = new Assignment();
        assignment.setProblem(anotherProblem);
        assignment.setAuthority(authority);

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.findByProblemId(2L))
                .thenReturn(Optional.of(assignment));

        when(commentRepository.findByProblemId(2L))
                .thenReturn(List.of());

        List<CommentResponse> result =
                commentService.getComments(
                        2L,
                        "authority@gmail.com"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(commentRepository).findByProblemId(2L);
    }

    @Test
    void authorityCannotGetCommentsForAnotherAuthorityProblem() {

        Assignment assignment = new Assignment();
        assignment.setProblem(anotherProblem);
        assignment.setAuthority(authority);

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("anotherauthority@gmail.com"))
                .thenReturn(Optional.of(anotherAuthorityUser));

        when(assignmentRepository.findByProblemId(2L))
                .thenReturn(Optional.of(assignment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.getComments(
                        2L,
                        "anotherauthority@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(commentRepository, never()).findByProblemId(anyLong());
    }

    // =========================================================
    // AUTHORITY WITHOUT ASSIGNMENT
    // =========================================================

    @Test
    void authorityCannotGetCommentsWhenProblemIsNotAssigned() {

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.findByProblemId(2L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.getComments(
                        2L,
                        "authority@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(commentRepository, never()).findByProblemId(anyLong());
    }

    // =========================================================
    // PROBLEM NOT FOUND
    // =========================================================

    @Test
    void addCommentFailsWhenProblemDoesNotExist() {

        when(problemRepository.findById(999L))
                .thenReturn(Optional.empty());

        CommentRequest request = new CommentRequest();
        request.setText("Test");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.addComment(
                        999L,
                        request,
                        "citizen@gmail.com"
                )
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(userRepository, never()).findByEmail(anyString());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getCommentsFailsWhenProblemDoesNotExist() {

        when(problemRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.getComments(
                        999L,
                        "citizen@gmail.com"
                )
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(userRepository, never()).findByEmail(anyString());
        verify(commentRepository, never()).findByProblemId(anyLong());
    }

    // =========================================================
    // USER NOT FOUND
    // =========================================================

    @Test
    void addCommentFailsWhenUserDoesNotExist() {

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        CommentRequest request = new CommentRequest();
        request.setText("Test");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.addComment(
                        1L,
                        request,
                        "unknown@gmail.com"
                )
        );

        assertEquals(401, exception.getStatusCode().value());

        verify(commentRepository, never()).save(any());
    }

    @Test
    void getCommentsFailsWhenUserDoesNotExist() {

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> commentService.getComments(
                        1L,
                        "unknown@gmail.com"
                )
        );

        assertEquals(401, exception.getStatusCode().value());

        verify(commentRepository, never()).findByProblemId(anyLong());
    }

    // =========================================================
    // COMMENT SAVE VERIFICATION
    // =========================================================

    @Test
    void commentIsSavedWithProblemTextAndCreatedAt() {

        CommentRequest request = new CommentRequest();
        request.setText("Road needs urgent repair.");

        Comment savedComment = new Comment();
        savedComment.setId(300L);
        savedComment.setProblem(citizenProblem);
        savedComment.setText(request.getText());
        savedComment.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(savedComment);

        commentService.addComment(
                1L,
                request,
                "citizen@gmail.com"
        );

        ArgumentCaptor<Comment> captor =
                ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(captor.capture());

        Comment savedEntity = captor.getValue();

        assertEquals(citizenProblem, savedEntity.getProblem());
        assertEquals(
                "Road needs urgent repair.",
                savedEntity.getText()
        );
        assertNotNull(savedEntity.getCreatedAt());
    }

    // =========================================================
    // NOTIFICATION VERIFICATION
    // =========================================================

    @Test
    void commentByAnotherUserNotifiesProblemOwner() {

        CommentRequest request = new CommentRequest();
        request.setText("I also noticed this issue.");

        Comment savedComment = new Comment();
        savedComment.setId(400L);
        savedComment.setProblem(citizenProblem);
        savedComment.setText(request.getText());
        savedComment.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(savedComment);

        commentService.addComment(
                1L,
                request,
                "moderator@gmail.com"
        );

        verify(notificationService).createNotification(
                eq("A new comment was added to your problem #1."),
                eq(NotificationType.COMMENT_ADDED),
                eq("citizen@gmail.com")
        );
    }
}
package com.localproblemsolver.service;

import com.localproblemsolver.dto.AssignmentResponse;
import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.AssignmentStatus;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.Category;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.AuthorityRepository;
import com.localproblemsolver.repository.ProblemRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProblemService problemService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AssignmentService assignmentService;

    private Category category;
    private Category otherCategory;

    private Authority authority;
    private Authority secondAuthority;

    private User moderator;
    private User authorityUser;
    private User secondAuthorityUser;
    private User citizen;

    private Problem problem;

    @BeforeEach
    void setUp() {
        category = createCategory(1L, "Roads");
        otherCategory = createCategory(2L, "Garbage");

        authority = createAuthority(
                10L,
                "Road Department",
                category,
                "Nashik"
        );

        secondAuthority = createAuthority(
                20L,
                "Road Department 2",
                category,
                "Nashik"
        );

        moderator = new User(
                "Moderator",
                "moderator@gmail.com",
                "password",
                Role.MODERATOR
        );

        authorityUser = new User(
                "Authority User",
                "authority@gmail.com",
                "password",
                Role.AUTHORITY
        );
        authorityUser.setAuthority(authority);

        secondAuthorityUser = new User(
                "Second Authority User",
                "authority2@gmail.com",
                "password",
                Role.AUTHORITY
        );
        secondAuthorityUser.setAuthority(secondAuthority);

        citizen = new User(
                "Citizen",
                "citizen@gmail.com",
                "password",
                Role.CITIZEN
        );

        problem = createProblem(
                100L,
                "Large pothole",
                category,
                "Nashik Road",
                ProblemStatus.VALIDATED
        );
        problem.setUser(citizen);
    }

    // =========================================================
    // ASSIGN PROBLEM
    // =========================================================

    @Test
    void testAssignProblemSuccessfully() {
        when(problemRepository.findById(100L))
                .thenReturn(Optional.of(problem));

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.empty());

        when(authorityRepository.findByCategoryId(1L))
                .thenReturn(List.of(authority));

        when(assignmentRepository.save(any(Assignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(userRepository.findByAuthorityId(10L))
                .thenReturn(Optional.of(authorityUser));

        AssignmentResponse result =
                assignmentService.assignProblem(
                        100L,
                        "moderator@gmail.com"
                );

        assertNotNull(result);
        assertEquals(100L, result.getProblemId());
        assertEquals(10L, result.getAuthority().getId());

        verify(problemService).changeStatus(
                100L,
                ProblemStatus.ASSIGNED,
                "moderator@gmail.com"
        );

        verify(notificationService).createNotification(
                contains("Problem #100"),
                any(),
                eq("authority@gmail.com")
        );
    }

    @Test
    void testAssignProblemNotFound() {
        when(problemRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.assignProblem(
                        100L,
                        "moderator@gmail.com"
                )
        );

        verifyNoInteractions(authorityRepository);
    }

    @Test
    void testAssignProblemWithoutCategory() {
        problem.setCategory(null);

        when(problemRepository.findById(100L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.assignProblem(
                        100L,
                        "moderator@gmail.com"
                )
        );

        verifyNoInteractions(authorityRepository);
    }

    @Test
    void testAssignProblemNotValidated() {
        problem.setStatus(ProblemStatus.OPEN);

        when(problemRepository.findById(100L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.assignProblem(
                        100L,
                        "moderator@gmail.com"
                )
        );

        verifyNoInteractions(authorityRepository);
    }

    @Test
    void testAssignProblemAlreadyAssigned() {
        Assignment existingAssignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );

        when(problemRepository.findById(100L))
                .thenReturn(Optional.of(problem));

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(existingAssignment));

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.assignProblem(
                        100L,
                        "moderator@gmail.com"
                )
        );

        verifyNoInteractions(authorityRepository);
    }

    @Test
    void testAssignProblemNoAuthorityForLocation() {
        Authority wrongZoneAuthority = createAuthority(
                30L,
                "Road Department Pune",
                category,
                "Pune"
        );

        when(problemRepository.findById(100L))
                .thenReturn(Optional.of(problem));

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.empty());

        when(authorityRepository.findByCategoryId(1L))
                .thenReturn(List.of(wrongZoneAuthority));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.assignProblem(
                        100L,
                        "moderator@gmail.com"
                )
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void testAssignProblemAuthorityHasNoLinkedUser() {
        when(problemRepository.findById(100L))
                .thenReturn(Optional.of(problem));

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.empty());

        when(authorityRepository.findByCategoryId(1L))
                .thenReturn(List.of(authority));

        when(assignmentRepository.save(any(Assignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(userRepository.findByAuthorityId(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.assignProblem(
                        100L,
                        "moderator@gmail.com"
                )
        );
    }

    // =========================================================
    // ACCEPT ASSIGNMENT
    // =========================================================

    @Test
    void testAcceptAssignmentSuccessfully() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );
        problem.setStatus(ProblemStatus.ASSIGNED);

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.save(any(Assignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AssignmentResponse result =
                assignmentService.acceptAssignment(
                        100L,
                        "authority@gmail.com"
                );

        assertNotNull(result);
        assertEquals(
                AssignmentStatus.ACCEPTED,
                assignment.getStatus()
        );

        verify(problemService).changeStatus(
                100L,
                ProblemStatus.IN_PROGRESS,
                "authority@gmail.com"
        );
    }

    @Test
    void testAcceptAssignmentNotFound() {
        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.acceptAssignment(
                        100L,
                        "authority@gmail.com"
                )
        );
    }

    @Test
    void testAcceptAssignmentUserNotFound() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.acceptAssignment(
                        100L,
                        "authority@gmail.com"
                )
        );
    }

    @Test
    void testAcceptAssignmentByCitizen() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.acceptAssignment(
                        100L,
                        "citizen@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void testAcceptAssignmentWithoutLinkedAuthority() {
        User userWithoutAuthority = new User(
                "Authority User",
                "authority@gmail.com",
                "password",
                Role.AUTHORITY
        );

        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(userWithoutAuthority));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.acceptAssignment(
                        100L,
                        "authority@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void testWrongAuthorityCannotAcceptAssignment() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority2@gmail.com"))
                .thenReturn(Optional.of(secondAuthorityUser));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.acceptAssignment(
                        100L,
                        "authority2@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void testCannotAcceptNonPendingAssignment() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.DECLINED
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.acceptAssignment(
                        100L,
                        "authority@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testCannotAcceptWhenProblemNotAssigned() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );
        problem.setStatus(ProblemStatus.VALIDATED);

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.acceptAssignment(
                        100L,
                        "authority@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    // =========================================================
    // DECLINE ASSIGNMENT
    // =========================================================

    @Test
    void testDeclineAssignmentSuccessfully() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );
        problem.setStatus(ProblemStatus.ASSIGNED);

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.save(any(Assignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AssignmentResponse result =
                assignmentService.declineAssignment(
                        100L,
                        "authority@gmail.com"
                );

        assertNotNull(result);
        assertEquals(
                AssignmentStatus.DECLINED,
                assignment.getStatus()
        );

        verify(problemService, never()).changeStatus(
                anyLong(),
                any(),
                anyString()
        );
    }

    @Test
    void testWrongAuthorityCannotDeclineAssignment() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority2@gmail.com"))
                .thenReturn(Optional.of(secondAuthorityUser));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.declineAssignment(
                        100L,
                        "authority2@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void testCannotDeclineNonPendingAssignment() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.ACCEPTED
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.declineAssignment(
                        100L,
                        "authority@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testCannotDeclineWhenProblemNotAssigned() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );
        problem.setStatus(ProblemStatus.VALIDATED);

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.declineAssignment(
                        100L,
                        "authority@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    // =========================================================
    // REASSIGNMENT
    // =========================================================

    @Test
    void testReassignProblemSuccessfully() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.DECLINED
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(authorityRepository.findById(20L))
                .thenReturn(Optional.of(secondAuthority));

        when(userRepository.findByAuthorityId(20L))
                .thenReturn(Optional.of(secondAuthorityUser));

        when(assignmentRepository.save(any(Assignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AssignmentResponse result =
                assignmentService.reassignProblem(
                        100L,
                        20L
                );

        assertNotNull(result);
        assertEquals(
                20L,
                assignment.getAuthority().getId()
        );
        assertEquals(
                AssignmentStatus.PENDING,
                assignment.getStatus()
        );

        verify(notificationService).createNotification(
                contains("reassigned"),
                any(),
                eq("authority2@gmail.com")
        );
    }

    @Test
    void testReassignOnlyDeclinedAssignment() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.reassignProblem(
                        100L,
                        20L
                )
        );

        assertEquals(400, exception.getStatusCode().value());
        verifyNoInteractions(authorityRepository);
    }

    @Test
    void testReassignAuthorityNotFound() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.DECLINED
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(authorityRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.reassignProblem(
                        100L,
                        999L
                )
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void testReassignAssignmentNotFound() {
        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.reassignProblem(
                        100L,
                        20L
                )
        );

        assertEquals(404, exception.getStatusCode().value());
        verifyNoInteractions(authorityRepository);
    }

    @Test
    void testReassignCategoryMismatch() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.DECLINED
        );

        Authority garbageAuthority = createAuthority(
                30L,
                "Garbage Department",
                otherCategory,
                "Nashik"
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(authorityRepository.findById(30L))
                .thenReturn(Optional.of(garbageAuthority));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.reassignProblem(
                        100L,
                        30L
                )
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testReassignZoneMismatch() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.DECLINED
        );

        Authority puneAuthority = createAuthority(
                30L,
                "Road Department Pune",
                category,
                "Pune"
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(authorityRepository.findById(30L))
                .thenReturn(Optional.of(puneAuthority));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.reassignProblem(
                        100L,
                        30L
                )
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testCannotReassignToSameAuthority() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.DECLINED
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(authorityRepository.findById(10L))
                .thenReturn(Optional.of(authority));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.reassignProblem(
                        100L,
                        10L
                )
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testReassignAuthorityWithoutLinkedUser() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.DECLINED
        );

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        when(authorityRepository.findById(20L))
                .thenReturn(Optional.of(secondAuthority));

        when(userRepository.findByAuthorityId(20L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.reassignProblem(
                        100L,
                        20L
                )
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    // =========================================================
    // GET ASSIGNMENT
    // =========================================================

    @Test
    void testGetAssignmentSuccessfully() {
        Assignment assignment = createAssignment(
                problem,
                authority,
                AssignmentStatus.PENDING
        );
        assignment.setId(500L);

        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.of(assignment));

        AssignmentResponse result =
                assignmentService.getAssignment(100L);

        assertNotNull(result);
        assertEquals(500L, result.getId());
        assertEquals(100L, result.getProblemId());
        assertEquals(10L, result.getAuthority().getId());
        assertEquals("Road Department", result.getAuthority().getName());
        assertEquals("Nashik", result.getAuthority().getZone());
    }

    @Test
    void testGetAssignmentNotFound() {
        when(assignmentRepository.findByProblemId(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> assignmentService.getAssignment(100L)
        );
    }

    // =========================================================
    // TEST DATA HELPERS
    // =========================================================

    private Category createCategory(
            Long id,
            String name) {

        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(name + " category");

        return category;
    }

    private Authority createAuthority(
            Long id,
            String name,
            Category category,
            String zone) {

        Authority authority =
                new Authority(name, category, zone);

        authority.setId(id);

        return authority;
    }

    private Problem createProblem(
            Long id,
            String title,
            Category category,
            String location,
            ProblemStatus status) {

        Problem problem = new Problem();

        problem.setId(id);
        problem.setTitle(title);
        problem.setDescription("Test problem description");
        problem.setCategory(category);
        problem.setLocation(location);
        problem.setStatus(status);

        return problem;
    }

    private Assignment createAssignment(
            Problem problem,
            Authority authority,
            AssignmentStatus status) {

        Assignment assignment = new Assignment();

        assignment.setId(500L);
        assignment.setProblem(problem);
        assignment.setAuthority(authority);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setStatus(status);

        return assignment;
    }
}

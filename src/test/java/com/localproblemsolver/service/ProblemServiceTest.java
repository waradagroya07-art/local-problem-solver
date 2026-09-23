package com.localproblemsolver.service;

import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.Category;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.StatusHistory;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.exception.InvalidStatusTransitionException;
import com.localproblemsolver.exception.ProblemNotFoundException;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.CategoryRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.StatusHistoryRepository;
import com.localproblemsolver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProblemServiceTest {

    private ProblemRepository problemRepository;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private StatusHistoryRepository statusHistoryRepository;
    private PriorityCalculationService priorityCalculationService;
    private AssignmentRepository assignmentRepository;
    private NotificationService notificationService;

    private ProblemService problemService;

    @BeforeEach
    void setUp() {

        problemRepository = mock(ProblemRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        userRepository = mock(UserRepository.class);
        statusHistoryRepository = mock(StatusHistoryRepository.class);
        priorityCalculationService =
                mock(PriorityCalculationService.class);
        assignmentRepository =
                mock(AssignmentRepository.class);
        notificationService =
                mock(NotificationService.class);

        problemService = new ProblemService(
                problemRepository,
                categoryRepository,
                userRepository,
                statusHistoryRepository,
                priorityCalculationService,
                assignmentRepository,
                notificationService
        );
    }

    // ============================================================
    // 1. SAVE PROBLEM SUCCESSFULLY
    // ============================================================

    @Test
    void testSaveProblemSuccessfully() {

        Category category = createCategory(1L, "Road");

        User citizen = createCitizen(
                "citizen@gmail.com"
        );

        Problem problem = new Problem();

        problem.setTitle("Pothole");
        problem.setDescription("Large pothole");
        problem.setCategory(null);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(userRepository.findByEmail(
                "citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(priorityCalculationService
                .calculatePriority(problem))
                .thenReturn(Priority.HIGH);

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.saveProblem(
                        problem,
                        1L,
                        "citizen@gmail.com"
                );

        assertNotNull(result);

        assertEquals(
                category,
                problem.getCategory()
        );

        assertEquals(
                citizen,
                problem.getUser()
        );

        assertEquals(
                ProblemStatus.OPEN,
                problem.getStatus()
        );

        assertEquals(
                Priority.HIGH,
                problem.getPriority()
        );

        assertNotNull(problem.getCreatedAt());
        assertNotNull(problem.getUpdatedAt());

        verify(notificationService)
                .createNotification(
                        eq("Your problem has been reported successfully."),
                        eq(NotificationType.PROBLEM_CREATED),
                        eq("citizen@gmail.com")
                );
    }

    // ============================================================
    // 2. SAVE PROBLEM WITHOUT CATEGORY
    // ============================================================

    @Test
    void testSaveProblemWithoutCategory() {

        User citizen =
                createCitizen("citizen@gmail.com");

        Problem problem = new Problem();

        when(userRepository.findByEmail(
                "citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(priorityCalculationService
                .calculatePriority(problem))
                .thenReturn(Priority.LOW);

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.saveProblem(
                        problem,
                        null,
                        "citizen@gmail.com"
                );

        assertNotNull(result);

        assertEquals(
                ProblemStatus.OPEN,
                problem.getStatus()
        );

        assertEquals(
                Priority.LOW,
                problem.getPriority()
        );

        verify(categoryRepository, never())
                .findById(any());
    }

    // ============================================================
    // 3. SAVE PROBLEM - CATEGORY NOT FOUND
    // ============================================================

    @Test
    void testSaveProblemCategoryNotFound() {

        Problem problem = new Problem();

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> problemService.saveProblem(
                        problem,
                        999L,
                        "citizen@gmail.com"
                )
        );

        verify(problemRepository, never())
                .save(any(Problem.class));
    }

    // ============================================================
    // 4. SAVE PROBLEM - USER NOT FOUND
    // ============================================================

    @Test
    void testSaveProblemUserNotFound() {

        Problem problem = new Problem();

        when(userRepository.findByEmail(
                "unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> problemService.saveProblem(
                        problem,
                        null,
                        "unknown@gmail.com"
                )
        );

        verify(problemRepository, never())
                .save(any(Problem.class));
    }

    // ============================================================
    // 5. FIND ALL PROBLEMS
    // ============================================================

    @Test
    void testFindAllProblems() {

        Problem problem1 =
                createProblem(
                        1L,
                        ProblemStatus.OPEN
                );

        Problem problem2 =
                createProblem(
                        2L,
                        ProblemStatus.VALIDATED
                );

        when(problemRepository.findAll())
                .thenReturn(List.of(
                        problem1,
                        problem2
                ));

        var result =
                problemService.findAllProblems();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ============================================================
    // 6. FIND PROBLEM BY ID
    // ============================================================

    @Test
    void testFindProblemById() {

        Problem problem =
                createProblem(
                        10L,
                        ProblemStatus.OPEN
                );

        when(problemRepository.findById(10L))
                .thenReturn(Optional.of(problem));

        var result =
                problemService.findProblemById(10L);

        assertNotNull(result);
    }

    // ============================================================
    // 7. FIND PROBLEM BY ID - NOT FOUND
    // ============================================================

    @Test
    void testFindProblemByIdNotFound() {

        when(problemRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProblemNotFoundException.class,
                () -> problemService.findProblemById(999L)
        );
    }

    // ============================================================
    // 8. OPEN -> VALIDATED
    // ============================================================

    @Test
    void testOpenToValidated() {

        Problem problem =
                createProblem(
                        20L,
                        ProblemStatus.OPEN
                );

        User moderator =
                createUser(
                        Role.MODERATOR,
                        "moderator@gmail.com"
                );

        when(problemRepository.findById(20L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.changeStatus(
                        20L,
                        ProblemStatus.VALIDATED,
                        "moderator@gmail.com"
                );

        assertEquals(
                ProblemStatus.VALIDATED,
                result.getStatus()
        );

        verify(statusHistoryRepository)
                .save(any(StatusHistory.class));

        verify(notificationService)
                .createNotification(
                        contains("status"),
                        eq(NotificationType.STATUS_CHANGED),
                        any(String.class)
                );
    }

    // ============================================================
    // 9. VALIDATED -> ASSIGNED
    // ============================================================

    @Test
    void testValidatedToAssigned() {

        Problem problem =
                createProblem(
                        21L,
                        ProblemStatus.VALIDATED
                );

        User moderator =
                createUser(
                        Role.MODERATOR,
                        "moderator@gmail.com"
                );

        when(problemRepository.findById(21L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.changeStatus(
                        21L,
                        ProblemStatus.ASSIGNED,
                        "moderator@gmail.com"
                );

        assertEquals(
                ProblemStatus.ASSIGNED,
                result.getStatus()
        );

        verify(statusHistoryRepository)
                .save(any(StatusHistory.class));
    }

    // ============================================================
    // 10. ASSIGNED -> IN_PROGRESS
    // ============================================================

    @Test
    void testAssignedToInProgress() {

        Problem problem =
                createProblem(
                        22L,
                        ProblemStatus.ASSIGNED
                );

        User authority =
                createAuthorityUser(
                        100L,
                        "authority@gmail.com"
                );

        when(problemRepository.findById(22L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "authority@gmail.com"))
                .thenReturn(Optional.of(authority));

        when(assignmentRepository
                .existsByProblemIdAndAuthorityId(
                        22L,
                        100L))
                .thenReturn(true);

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.changeStatus(
                        22L,
                        ProblemStatus.IN_PROGRESS,
                        "authority@gmail.com"
                );

        assertEquals(
                ProblemStatus.IN_PROGRESS,
                result.getStatus()
        );
    }

    // ============================================================
    // 11. IN_PROGRESS -> RESOLVED
    // ============================================================

    @Test
    void testInProgressToResolved() {

        Problem problem =
                createProblem(
                        23L,
                        ProblemStatus.IN_PROGRESS
                );

        User authority =
                createAuthorityUser(
                        101L,
                        "authority@gmail.com"
                );

        when(problemRepository.findById(23L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "authority@gmail.com"))
                .thenReturn(Optional.of(authority));

        when(assignmentRepository
                .existsByProblemIdAndAuthorityId(
                        23L,
                        101L))
                .thenReturn(true);

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.changeStatus(
                        23L,
                        ProblemStatus.RESOLVED,
                        "authority@gmail.com"
                );

        assertEquals(
                ProblemStatus.RESOLVED,
                result.getStatus()
        );
    }

    // ============================================================
    // 12. RESOLVED -> CLOSED
    // ============================================================

    @Test
    void testResolvedToClosed() {

        Problem problem =
                createProblem(
                        24L,
                        ProblemStatus.RESOLVED
                );

        User citizen =
                createCitizen(
                        "citizen@gmail.com"
                );

        problem.setUser(citizen);

        when(problemRepository.findById(24L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.confirmProblem(
                        24L,
                        "citizen@gmail.com"
                );

        assertEquals(
                ProblemStatus.CLOSED,
                result.getStatus()
        );
    }

    // ============================================================
    // 13. RESOLVED -> REOPENED
    // ============================================================

    @Test
    void testResolvedToReopened() {

        Problem problem =
                createProblem(
                        25L,
                        ProblemStatus.RESOLVED
                );

        User citizen =
                createCitizen(
                        "citizen@gmail.com"
                );

        problem.setUser(citizen);

        when(problemRepository.findById(25L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.reopenProblem(
                        25L,
                        "citizen@gmail.com"
                );

        assertEquals(
                ProblemStatus.REOPENED,
                result.getStatus()
        );
    }

    // ============================================================
    // 14. CLOSED -> REOPENED
    // ============================================================

    @Test
    void testClosedToReopened() {

        Problem problem =
                createProblem(
                        26L,
                        ProblemStatus.CLOSED
                );

        User moderator =
                createUser(
                        Role.MODERATOR,
                        "moderator@gmail.com"
                );

        when(problemRepository.findById(26L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.changeStatus(
                        26L,
                        ProblemStatus.REOPENED,
                        "moderator@gmail.com"
                );

        assertEquals(
                ProblemStatus.REOPENED,
                result.getStatus()
        );
    }

    // ============================================================
    // 15. REOPENED -> IN_PROGRESS
    // ============================================================

    @Test
    void testReopenedToInProgress() {

        Problem problem =
                createProblem(
                        27L,
                        ProblemStatus.REOPENED
                );

        User moderator =
                createUser(
                        Role.MODERATOR,
                        "moderator@gmail.com"
                );

        when(problemRepository.findById(27L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.changeStatus(
                        27L,
                        ProblemStatus.IN_PROGRESS,
                        "moderator@gmail.com"
                );

        assertEquals(
                ProblemStatus.IN_PROGRESS,
                result.getStatus()
        );
    }

    // ============================================================
    // 16. INVALID STATUS TRANSITION
    // ============================================================

    @Test
    void testInvalidStatusTransition() {

        Problem problem =
                createProblem(
                        30L,
                        ProblemStatus.OPEN
                );

        when(problemRepository.findById(30L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> problemService.changeStatus(
                        30L,
                        ProblemStatus.CLOSED,
                        "moderator@gmail.com"
                )
        );

        verify(problemRepository, never())
                .save(any(Problem.class));
    }

    // ============================================================
    // 17. CHANGE STATUS - PROBLEM NOT FOUND
    // ============================================================

    @Test
    void testChangeStatusProblemNotFound() {

        when(problemRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProblemNotFoundException.class,
                () -> problemService.changeStatus(
                        999L,
                        ProblemStatus.VALIDATED,
                        "moderator@gmail.com"
                )
        );
    }

    // ============================================================
    // 18. CHANGE STATUS - USER NOT FOUND
    // ============================================================

    @Test
    void testChangeStatusUserNotFound() {

        Problem problem =
                createProblem(
                        31L,
                        ProblemStatus.OPEN
                );

        when(problemRepository.findById(31L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> problemService.changeStatus(
                        31L,
                        ProblemStatus.VALIDATED,
                        "unknown@gmail.com"
                )
        );
    }

    // ============================================================
    // 19. AUTHORITY WITHOUT LINKED AUTHORITY
    // ============================================================

    @Test
    void testAuthorityWithoutLinkedAuthorityCannotChangeStatus() {

        Problem problem =
                createProblem(
                        32L,
                        ProblemStatus.ASSIGNED
                );

        User authority = mock(User.class);

        when(authority.getRole())
                .thenReturn(Role.AUTHORITY);

        when(authority.getAuthority())
                .thenReturn(null);

        when(userRepository.findByEmail(
                "authority@gmail.com"))
                .thenReturn(Optional.of(authority));

        when(problemRepository.findById(32L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                ResponseStatusException.class,
                () -> problemService.changeStatus(
                        32L,
                        ProblemStatus.IN_PROGRESS,
                        "authority@gmail.com"
                )
        );
    }

    // ============================================================
    // 20. AUTHORITY NOT ASSIGNED TO PROBLEM
    // ============================================================

    @Test
    void testAuthorityNotAssignedCannotChangeStatus() {

        Problem problem =
                createProblem(
                        33L,
                        ProblemStatus.ASSIGNED
                );

        User authority =
                createAuthorityUser(
                        200L,
                        "authority@gmail.com"
                );

        when(problemRepository.findById(33L))
                .thenReturn(Optional.of(problem));

        when(userRepository.findByEmail(
                "authority@gmail.com"))
                .thenReturn(Optional.of(authority));

        when(assignmentRepository
                .existsByProblemIdAndAuthorityId(
                        33L,
                        200L))
                .thenReturn(false);

        assertThrows(
                ResponseStatusException.class,
                () -> problemService.changeStatus(
                        33L,
                        ProblemStatus.IN_PROGRESS,
                        "authority@gmail.com"
                )
        );
    }

    // ============================================================
    // 21. CITIZEN CONFIRM - NOT OWNER
    // ============================================================

    @Test
    void testCitizenCannotConfirmOtherUsersProblem() {

        User owner =
                createCitizen(
                        "owner@gmail.com"
                );

        Problem problem =
                createProblem(
                        34L,
                        ProblemStatus.RESOLVED
                );

        problem.setUser(owner);

        when(problemRepository.findById(34L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                ResponseStatusException.class,
                () -> problemService.confirmProblem(
                        34L,
                        "other@gmail.com"
                )
        );
    }

    // ============================================================
    // 22. CITIZEN CONFIRM - NOT RESOLVED
    // ============================================================

    @Test
    void testCannotConfirmUnresolvedProblem() {

        User citizen =
                createCitizen(
                        "citizen@gmail.com"
                );

        Problem problem =
                createProblem(
                        35L,
                        ProblemStatus.IN_PROGRESS
                );

        problem.setUser(citizen);

        when(problemRepository.findById(35L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                ResponseStatusException.class,
                () -> problemService.confirmProblem(
                        35L,
                        "citizen@gmail.com"
                )
        );
    }

    // ============================================================
    // 23. CITIZEN REOPEN - NOT OWNER
    // ============================================================

    @Test
    void testCitizenCannotReopenOtherUsersProblem() {

        User owner =
                createCitizen(
                        "owner@gmail.com"
                );

        Problem problem =
                createProblem(
                        36L,
                        ProblemStatus.RESOLVED
                );

        problem.setUser(owner);

        when(problemRepository.findById(36L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                ResponseStatusException.class,
                () -> problemService.reopenProblem(
                        36L,
                        "other@gmail.com"
                )
        );
    }

    // ============================================================
    // 24. CITIZEN REOPEN - NOT RESOLVED
    // ============================================================

    @Test
    void testCannotReopenUnresolvedProblem() {

        User citizen =
                createCitizen(
                        "citizen@gmail.com"
                );

        Problem problem =
                createProblem(
                        37L,
                        ProblemStatus.CLOSED
                );

        problem.setUser(citizen);

        when(problemRepository.findById(37L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                ResponseStatusException.class,
                () -> problemService.reopenProblem(
                        37L,
                        "citizen@gmail.com"
                )
        );
    }

    // ============================================================
    // 25. MARK AS DUPLICATE SUCCESSFULLY
    // ============================================================

    @Test
    void testMarkAsDuplicateSuccessfully() {

        Problem duplicateProblem =
                createProblem(
                        40L,
                        ProblemStatus.OPEN
                );

        Problem originalProblem =
                createProblem(
                        41L,
                        ProblemStatus.VALIDATED
                );

        User moderator =
                createUser(
                        Role.MODERATOR,
                        "moderator@gmail.com"
                );

        when(problemRepository.findById(40L))
                .thenReturn(Optional.of(duplicateProblem));

        when(problemRepository.findById(41L))
                .thenReturn(Optional.of(originalProblem));

        when(userRepository.findByEmail(
                "moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(problemRepository.save(
                duplicateProblem))
                .thenReturn(duplicateProblem);

        when(problemRepository.save(
                any(Problem.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(problemRepository.findById(
                40L))
                .thenReturn(Optional.of(duplicateProblem));

        Problem result =
                problemService.markAsDuplicate(
                        40L,
                        41L,
                        "moderator@gmail.com"
                );

        assertEquals(
                originalProblem,
                duplicateProblem.getDuplicateOf()
        );

        assertEquals(
                ProblemStatus.DUPLICATE,
                result.getStatus()
        );
    }

    // ============================================================
    // 26. MARK SELF AS DUPLICATE
    // ============================================================

    @Test
    void testCannotMarkProblemAsDuplicateOfItself() {

        Problem problem =
                createProblem(
                        50L,
                        ProblemStatus.OPEN
                );

        when(problemRepository.findById(50L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                IllegalArgumentException.class,
                () -> problemService.markAsDuplicate(
                        50L,
                        50L,
                        "moderator@gmail.com"
                )
        );
    }

    // ============================================================
    // 27. ORIGINAL ALREADY DUPLICATE
    // ============================================================

    @Test
    void testCannotUseDuplicateAsOriginal() {

        Problem duplicateProblem =
                createProblem(
                        51L,
                        ProblemStatus.OPEN
                );

        Problem originalProblem =
                createProblem(
                        52L,
                        ProblemStatus.DUPLICATE
                );

        when(problemRepository.findById(51L))
                .thenReturn(Optional.of(duplicateProblem));

        when(problemRepository.findById(52L))
                .thenReturn(Optional.of(originalProblem));

        assertThrows(
                IllegalArgumentException.class,
                () -> problemService.markAsDuplicate(
                        51L,
                        52L,
                        "moderator@gmail.com"
                )
        );
    }

    // ============================================================
    // 28. ONLY OPEN / VALIDATED CAN BE DUPLICATE
    // ============================================================

    @Test
    void testOnlyOpenOrValidatedCanBeMarkedDuplicate() {

        Problem duplicateProblem =
                createProblem(
                        53L,
                        ProblemStatus.IN_PROGRESS
                );

        Problem originalProblem =
                createProblem(
                        54L,
                        ProblemStatus.OPEN
                );

        when(problemRepository.findById(53L))
                .thenReturn(Optional.of(duplicateProblem));

        when(problemRepository.findById(54L))
                .thenReturn(Optional.of(originalProblem));

        assertThrows(
                IllegalArgumentException.class,
                () -> problemService.markAsDuplicate(
                        53L,
                        54L,
                        "moderator@gmail.com"
                )
        );
    }

    // ============================================================
    // 29. UPDATE CATEGORY
    // ============================================================

    @Test
    void testUpdateCategorySuccessfully() {

        Problem problem =
                createProblem(
                        60L,
                        ProblemStatus.OPEN
                );

        Category category =
                createCategory(
                        10L,
                        "Garbage"
                );

        when(problemRepository.findById(60L))
                .thenReturn(Optional.of(problem));

        when(categoryRepository.findById(10L))
                .thenReturn(Optional.of(category));

        when(problemRepository.save(problem))
                .thenReturn(problem);

        Problem result =
                problemService.updateCategory(
                        60L,
                        10L
                );

        assertEquals(
                category,
                result.getCategory()
        );

        assertNotNull(
                result.getUpdatedAt()
        );
    }

    // ============================================================
    // 30. UPDATE CATEGORY - PROBLEM NOT FOUND
    // ============================================================

    @Test
    void testUpdateCategoryProblemNotFound() {

        when(problemRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProblemNotFoundException.class,
                () -> problemService.updateCategory(
                        999L,
                        10L
                )
        );
    }

    // ============================================================
    // 31. UPDATE CATEGORY - CATEGORY NOT FOUND
    // ============================================================

    @Test
    void testUpdateCategoryCategoryNotFound() {

        Problem problem =
                createProblem(
                        61L,
                        ProblemStatus.OPEN
                );

        when(problemRepository.findById(61L))
                .thenReturn(Optional.of(problem));

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> problemService.updateCategory(
                        61L,
                        999L
                )
        );
    }

    // ============================================================
    // HELPER - CREATE PROBLEM
    // ============================================================

    private Problem createProblem(
            Long id,
            ProblemStatus status) {

        Problem problem =
                new Problem();

        problem.setId(id);
        problem.setStatus(status);
        problem.setTitle("Test Problem");
        problem.setDescription("Test Description");
        problem.setPriority(Priority.MEDIUM);
        problem.setCreatedAt(LocalDateTime.now());
        problem.setUpdatedAt(LocalDateTime.now());

        User citizen =
                createCitizen(
                        "owner@gmail.com"
                );

        problem.setUser(citizen);

        problem.setCategory(
                createCategory(
                        1L,
                        "Road"
                )
        );

        return problem;
    }

    // ============================================================
    // HELPER - CREATE CITIZEN
    // ============================================================

    private User createCitizen(
            String email) {

        User user =
                mock(User.class);

        when(user.getRole())
                .thenReturn(Role.CITIZEN);

        when(user.getEmail())
                .thenReturn(email);

        return user;
    }

    // ============================================================
    // HELPER - CREATE USER
    // ============================================================

    private User createUser(
            Role role,
            String email) {

        User user =
                mock(User.class);

        when(user.getRole())
                .thenReturn(role);

        when(user.getEmail())
                .thenReturn(email);

        return user;
    }

    // ============================================================
    // HELPER - CREATE AUTHORITY USER
    // ============================================================

    private User createAuthorityUser(
            Long authorityId,
            String email) {

        User user =
                mock(User.class);

        Authority authority =
                mock(Authority.class);

        when(authority.getId())
                .thenReturn(authorityId);

        when(user.getRole())
                .thenReturn(Role.AUTHORITY);

        when(user.getEmail())
                .thenReturn(email);

        when(user.getAuthority())
                .thenReturn(authority);

        return user;
    }

    // ============================================================
    // HELPER - CREATE CATEGORY
    // ============================================================

    private Category createCategory(
            Long id,
            String name) {

        Category category =
                mock(Category.class);

        when(category.getId())
                .thenReturn(id);

        when(category.getName())
                .thenReturn(name);

        return category;
    }
}
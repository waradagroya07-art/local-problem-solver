package com.localproblemsolver.service;

import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.Sla;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.SlaRepository;
import com.localproblemsolver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SlaServiceTest {

    private SlaRepository slaRepository;
    private ProblemRepository problemRepository;
    private NotificationService notificationService;
    private UserRepository userRepository;
    private AssignmentRepository assignmentRepository;

    private SlaService slaService;

    @BeforeEach
    void setUp() {

        slaRepository = mock(SlaRepository.class);
        problemRepository = mock(ProblemRepository.class);
        notificationService = mock(NotificationService.class);
        userRepository = mock(UserRepository.class);
        assignmentRepository = mock(AssignmentRepository.class);

        slaService = new SlaService(
                slaRepository,
                problemRepository,
                notificationService,
                userRepository,
                assignmentRepository
        );
    }

    // ============================================================
    // 1. CREATE SLA - CRITICAL
    // ============================================================

    @Test
    void testCreateSlaForCriticalPriority() {

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 20, 10, 0);

        Problem problem = new Problem();

        problem.setId(1L);
        problem.setPriority(Priority.CRITICAL);
        problem.setCreatedAt(createdAt);

        Sla savedSla = mock(Sla.class);

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(problem));

        when(slaRepository.findByProblemId(1L))
                .thenReturn(Optional.empty());

        when(slaRepository.save(any(Sla.class)))
                .thenReturn(savedSla);

        when(savedSla.getId())
                .thenReturn(10L);

        when(savedSla.getProblem())
                .thenReturn(problem);

        when(savedSla.getDeadline())
                .thenReturn(createdAt.plusHours(24));

        when(savedSla.isBreached())
                .thenReturn(false);

        var result =
                slaService.createSla(1L);

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getProblemId());
        assertEquals("CRITICAL", result.getPriority());
        assertEquals(
                createdAt.plusHours(24),
                result.getDeadline()
        );
        assertEquals(false, result.isBreached());
    }

    // ============================================================
    // 2. CREATE SLA - HIGH
    // ============================================================

    @Test
    void testCreateSlaForHighPriority() {

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 20, 10, 0);

        Problem problem = new Problem();

        problem.setId(2L);
        problem.setPriority(Priority.HIGH);
        problem.setCreatedAt(createdAt);

        Sla savedSla = mock(Sla.class);

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(problem));

        when(slaRepository.findByProblemId(2L))
                .thenReturn(Optional.empty());

        when(slaRepository.save(any(Sla.class)))
                .thenReturn(savedSla);

        when(savedSla.getId())
                .thenReturn(20L);

        when(savedSla.getProblem())
                .thenReturn(problem);

        when(savedSla.getDeadline())
                .thenReturn(createdAt.plusHours(48));

        when(savedSla.isBreached())
                .thenReturn(false);

        var result =
                slaService.createSla(2L);

        assertEquals(
                createdAt.plusHours(48),
                result.getDeadline()
        );
    }

    // ============================================================
    // 3. CREATE SLA - MEDIUM
    // ============================================================

    @Test
    void testCreateSlaForMediumPriority() {

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 20, 10, 0);

        Problem problem = new Problem();

        problem.setId(3L);
        problem.setPriority(Priority.MEDIUM);
        problem.setCreatedAt(createdAt);

        Sla savedSla = mock(Sla.class);

        when(problemRepository.findById(3L))
                .thenReturn(Optional.of(problem));

        when(slaRepository.findByProblemId(3L))
                .thenReturn(Optional.empty());

        when(slaRepository.save(any(Sla.class)))
                .thenReturn(savedSla);

        when(savedSla.getId())
                .thenReturn(30L);

        when(savedSla.getProblem())
                .thenReturn(problem);

        when(savedSla.getDeadline())
                .thenReturn(createdAt.plusHours(72));

        when(savedSla.isBreached())
                .thenReturn(false);

        var result =
                slaService.createSla(3L);

        assertEquals(
                createdAt.plusHours(72),
                result.getDeadline()
        );
    }

    // ============================================================
    // 4. CREATE SLA - LOW
    // ============================================================

    @Test
    void testCreateSlaForLowPriority() {

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 20, 10, 0);

        Problem problem = new Problem();

        problem.setId(4L);
        problem.setPriority(Priority.LOW);
        problem.setCreatedAt(createdAt);

        Sla savedSla = mock(Sla.class);

        when(problemRepository.findById(4L))
                .thenReturn(Optional.of(problem));

        when(slaRepository.findByProblemId(4L))
                .thenReturn(Optional.empty());

        when(slaRepository.save(any(Sla.class)))
                .thenReturn(savedSla);

        when(savedSla.getId())
                .thenReturn(40L);

        when(savedSla.getProblem())
                .thenReturn(problem);

        when(savedSla.getDeadline())
                .thenReturn(createdAt.plusHours(168));

        when(savedSla.isBreached())
                .thenReturn(false);

        var result =
                slaService.createSla(4L);

        assertEquals(
                createdAt.plusHours(168),
                result.getDeadline()
        );
    }

    // ============================================================
    // 5. PROBLEM NOT FOUND
    // ============================================================

    @Test
    void testCreateSlaProblemNotFound() {

        when(problemRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> slaService.createSla(999L)
        );
    }

    // ============================================================
    // 6. PRIORITY IS NULL
    // ============================================================

    @Test
    void testCreateSlaWithoutPriority() {

        Problem problem = new Problem();

        problem.setId(5L);
        problem.setPriority(null);
        problem.setCreatedAt(LocalDateTime.now());

        when(problemRepository.findById(5L))
                .thenReturn(Optional.of(problem));

        assertThrows(
                ResponseStatusException.class,
                () -> slaService.createSla(5L)
        );

        verify(slaRepository, never())
                .save(any(Sla.class));
    }

    // ============================================================
    // 7. SLA ALREADY EXISTS
    // ============================================================

    @Test
    void testCreateSlaWhenSlaAlreadyExists() {

        Problem problem = new Problem();

        problem.setId(6L);
        problem.setPriority(Priority.HIGH);
        problem.setCreatedAt(LocalDateTime.now());

        Sla existingSla = mock(Sla.class);

        when(problemRepository.findById(6L))
                .thenReturn(Optional.of(problem));

        when(slaRepository.findByProblemId(6L))
                .thenReturn(Optional.of(existingSla));

        assertThrows(
                ResponseStatusException.class,
                () -> slaService.createSla(6L)
        );

        verify(slaRepository, never())
                .save(any(Sla.class));
    }

    // ============================================================
    // 8. NULL CREATED AT
    // ============================================================

    @Test
    void testCreateSlaWithNullCreatedAt() {

        Problem problem = new Problem();

        problem.setId(7L);
        problem.setPriority(Priority.LOW);
        problem.setCreatedAt(null);

        Sla savedSla = mock(Sla.class);

        when(problemRepository.findById(7L))
                .thenReturn(Optional.of(problem));

        when(slaRepository.findByProblemId(7L))
                .thenReturn(Optional.empty());

        when(slaRepository.save(any(Sla.class)))
                .thenReturn(savedSla);

        when(savedSla.getId())
                .thenReturn(70L);

        when(savedSla.getProblem())
                .thenReturn(problem);

        when(savedSla.getDeadline())
                .thenReturn(LocalDateTime.now().plusHours(168));

        when(savedSla.isBreached())
                .thenReturn(false);

        var result =
                slaService.createSla(7L);

        assertEquals("LOW", result.getPriority());
    }

    // ============================================================
    // 9. CITIZEN CAN VIEW OWN SLA
    // ============================================================

    @Test
    void testCitizenCanViewOwnSla() {

        User citizen = mock(User.class);

        when(citizen.getId())
                .thenReturn(1L);

        when(citizen.getRole())
                .thenReturn(Role.CITIZEN);

        Problem problem = new Problem();

        problem.setId(10L);
        problem.setPriority(Priority.MEDIUM);
        problem.setUser(citizen);

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(10L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(sla.getProblem())
                .thenReturn(problem);

        when(sla.getId())
                .thenReturn(100L);

        when(sla.getDeadline())
                .thenReturn(LocalDateTime.now().plusDays(2));

        when(sla.isBreached())
                .thenReturn(false);

        var result =
                slaService.getSla(
                        10L,
                        "citizen@gmail.com"
                );

        assertEquals(100L, result.getId());
        assertEquals(10L, result.getProblemId());
        assertEquals("MEDIUM", result.getPriority());
    }

    // ============================================================
    // 10. CITIZEN CANNOT VIEW OTHER USER'S SLA
    // ============================================================

    @Test
    void testCitizenCannotViewOtherUsersSla() {

        User problemOwner = mock(User.class);
        User loggedInCitizen = mock(User.class);

        when(problemOwner.getId())
                .thenReturn(1L);

        when(loggedInCitizen.getId())
                .thenReturn(2L);

        when(loggedInCitizen.getRole())
                .thenReturn(Role.CITIZEN);

        Problem problem = new Problem();

        problem.setId(11L);
        problem.setPriority(Priority.MEDIUM);
        problem.setUser(problemOwner);

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(11L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("citizen2@gmail.com"))
                .thenReturn(Optional.of(loggedInCitizen));

        when(sla.getProblem())
                .thenReturn(problem);

        assertThrows(
                ResponseStatusException.class,
                () -> slaService.getSla(
                        11L,
                        "citizen2@gmail.com"
                )
        );
    }

    // ============================================================
    // 11. MODERATOR CAN VIEW SLA
    // ============================================================

    @Test
    void testModeratorCanViewSla() {

        User moderator = mock(User.class);

        when(moderator.getId())
                .thenReturn(20L);

        when(moderator.getRole())
                .thenReturn(Role.MODERATOR);

        Problem problem = new Problem();

        problem.setId(12L);
        problem.setPriority(Priority.HIGH);

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(12L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(sla.getProblem())
                .thenReturn(problem);

        when(sla.getId())
                .thenReturn(120L);

        when(sla.getDeadline())
                .thenReturn(LocalDateTime.now().plusDays(1));

        when(sla.isBreached())
                .thenReturn(false);

        var result =
                slaService.getSla(
                        12L,
                        "moderator@gmail.com"
                );

        assertEquals(120L, result.getId());
    }

    // ============================================================
    // 12. SUPER ADMIN CAN VIEW SLA
    // ============================================================

    @Test
    void testSuperAdminCanViewSla() {

        User admin = mock(User.class);

        when(admin.getId())
                .thenReturn(30L);

        when(admin.getRole())
                .thenReturn(Role.SUPER_ADMIN);

        Problem problem = new Problem();

        problem.setId(13L);
        problem.setPriority(Priority.CRITICAL);

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(13L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));

        when(sla.getProblem())
                .thenReturn(problem);

        when(sla.getId())
                .thenReturn(130L);

        when(sla.getDeadline())
                .thenReturn(LocalDateTime.now().plusDays(1));

        when(sla.isBreached())
                .thenReturn(false);

        var result =
                slaService.getSla(
                        13L,
                        "admin@gmail.com"
                );

        assertEquals(130L, result.getId());
    }

    // ============================================================
    // 13. AUTHORITY CAN VIEW ASSIGNED SLA
    // ============================================================

    @Test
    void testAuthorityCanViewAssignedSla() {

        User authorityUser = mock(User.class);
        Authority authority = mock(Authority.class);
        Assignment assignment = mock(Assignment.class);

        when(authorityUser.getId())
                .thenReturn(40L);

        when(authorityUser.getRole())
                .thenReturn(Role.AUTHORITY);

        when(authorityUser.getAuthority())
                .thenReturn(authority);

        when(authority.getId())
                .thenReturn(100L);

        Problem problem = new Problem();

        problem.setId(14L);
        problem.setPriority(Priority.HIGH);

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(14L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.findByProblemId(14L))
                .thenReturn(Optional.of(assignment));

        when(assignment.getAuthority())
                .thenReturn(authority);

        when(sla.getProblem())
                .thenReturn(problem);

        when(sla.getId())
                .thenReturn(140L);

        when(sla.getDeadline())
                .thenReturn(LocalDateTime.now().plusDays(1));

        when(sla.isBreached())
                .thenReturn(false);

        var result =
                slaService.getSla(
                        14L,
                        "authority@gmail.com"
                );

        assertEquals(140L, result.getId());
    }

    // ============================================================
    // 14. AUTHORITY CANNOT VIEW UNASSIGNED SLA
    // ============================================================

    @Test
    void testAuthorityCannotViewUnassignedSla() {

        User authorityUser = mock(User.class);
        Authority authority = mock(Authority.class);

        when(authorityUser.getId())
                .thenReturn(41L);

        when(authorityUser.getRole())
                .thenReturn(Role.AUTHORITY);

        when(authorityUser.getAuthority())
                .thenReturn(authority);

        Problem problem = new Problem();

        problem.setId(15L);
        problem.setPriority(Priority.HIGH);

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(15L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("authority2@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.findByProblemId(15L))
                .thenReturn(Optional.empty());

        when(sla.getProblem())
                .thenReturn(problem);

        assertThrows(
                ResponseStatusException.class,
                () -> slaService.getSla(
                        15L,
                        "authority2@gmail.com"
                )
        );
    }

    // ============================================================
    // 15. SLA BREACH
    // ============================================================

    @Test
    void testSlaBreachIsDetected() {

        User citizen = mock(User.class);

        when(citizen.getId())
                .thenReturn(50L);

        when(citizen.getRole())
                .thenReturn(Role.CITIZEN);

        when(citizen.getEmail())
                .thenReturn("citizen@gmail.com");

        Problem problem = new Problem();

        problem.setId(16L);
        problem.setPriority(Priority.HIGH);
        problem.setUser(citizen);

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(16L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(sla.getProblem())
                .thenReturn(problem);

        when(sla.getId())
                .thenReturn(160L);

        when(sla.getDeadline())
                .thenReturn(LocalDateTime.now().minusHours(1));

        when(sla.isBreached())
                .thenReturn(false)
                .thenReturn(true);

        var result =
                slaService.getSla(
                        16L,
                        "citizen@gmail.com"
                );

        assertEquals(true, result.isBreached());

        verify(slaRepository)
                .save(sla);

        verify(notificationService)
                .createNotification(
                        eq("The SLA for your problem #16 has been breached."),
                        eq(NotificationType.SLA_BREACHED),
                        eq("citizen@gmail.com")
                );
    }

    // ============================================================
    // 16. ALREADY BREACHED SLA IS NOT SAVED AGAIN
    // ============================================================

    @Test
    void testAlreadyBreachedSlaIsNotProcessedAgain() {

        Sla sla = mock(Sla.class);

        when(slaRepository.findAll())
                .thenReturn(List.of(sla));

        when(sla.isBreached())
                .thenReturn(true);

        slaService.checkAllSlaBreaches();

        verify(slaRepository, never())
                .save(sla);

        verifyNoInteractions(notificationService);
    }

    // ============================================================
    // 17. SCHEDULED CHECK PROCESSES ALL SLAS
    // ============================================================

    @Test
    void testCheckAllSlaBreachesProcessesAllSlas() {

        Sla sla1 = mock(Sla.class);
        Sla sla2 = mock(Sla.class);

        when(slaRepository.findAll())
                .thenReturn(List.of(sla1, sla2));

        when(sla1.isBreached())
                .thenReturn(true);

        when(sla2.isBreached())
                .thenReturn(true);

        slaService.checkAllSlaBreaches();

        verify(slaRepository, never())
                .save(sla1);

        verify(slaRepository, never())
                .save(sla2);
    }

    // ============================================================
    // 18. SLA NOT FOUND
    // ============================================================

    @Test
    void testGetSlaNotFound() {

        when(slaRepository.findByProblemId(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> slaService.getSla(
                        999L,
                        "citizen@gmail.com"
                )
        );
    }

    // ============================================================
    // 19. USER NOT FOUND
    // ============================================================

    @Test
    void testGetSlaUserNotFound() {

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(20L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> slaService.getSla(
                        20L,
                        "unknown@gmail.com"
                )
        );
    }

    // ============================================================
    // 20. AUTHORITY WITHOUT LINKED AUTHORITY
    // ============================================================

    @Test
    void testAuthorityWithoutLinkedAuthorityCannotViewSla() {

        User authorityUser = mock(User.class);

        when(authorityUser.getRole())
                .thenReturn(Role.AUTHORITY);

        when(authorityUser.getAuthority())
                .thenReturn(null);

        Problem problem = new Problem();

        problem.setId(21L);
        problem.setPriority(Priority.HIGH);

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(21L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("authority3@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(sla.getProblem())
                .thenReturn(problem);

        assertThrows(
                ResponseStatusException.class,
                () -> slaService.getSla(
                        21L,
                        "authority3@gmail.com"
                )
        );
    }

    // ============================================================
    // 21. DIFFERENT AUTHORITY CANNOT VIEW SLA
    // ============================================================

    @Test
    void testDifferentAuthorityCannotViewSla() {

        User authorityUser = mock(User.class);

        Authority userAuthority =
                mock(Authority.class);

        Authority assignedAuthority =
                mock(Authority.class);

        Assignment assignment =
                mock(Assignment.class);

        when(authorityUser.getRole())
                .thenReturn(Role.AUTHORITY);

        when(authorityUser.getAuthority())
                .thenReturn(userAuthority);

        when(userAuthority.getId())
                .thenReturn(100L);

        when(assignedAuthority.getId())
                .thenReturn(200L);

        Problem problem = new Problem();

        problem.setId(22L);
        problem.setPriority(Priority.HIGH);

        Sla sla = mock(Sla.class);

        when(slaRepository.findByProblemId(22L))
                .thenReturn(Optional.of(sla));

        when(userRepository.findByEmail("authority4@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.findByProblemId(22L))
                .thenReturn(Optional.of(assignment));

        when(assignment.getAuthority())
                .thenReturn(assignedAuthority);

        when(sla.getProblem())
                .thenReturn(problem);

        assertThrows(
                ResponseStatusException.class,
                () -> slaService.getSla(
                        22L,
                        "authority4@gmail.com"
                )
        );
    }
}
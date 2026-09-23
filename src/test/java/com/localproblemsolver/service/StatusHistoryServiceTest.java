package com.localproblemsolver.service;

import com.localproblemsolver.dto.StatusHistoryResponse;
import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.StatusHistory;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.exception.ProblemNotFoundException;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.StatusHistoryRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusHistoryServiceTest {

    @Mock
    private StatusHistoryRepository statusHistoryRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @InjectMocks
    private StatusHistoryService statusHistoryService;

    private User citizen;
    private User anotherCitizen;
    private User moderator;
    private User superAdmin;
    private User authorityUser;
    private User anotherAuthorityUser;

    private Authority authority;
    private Authority anotherAuthority;

    private Problem citizenProblem;
    private Problem anotherProblem;

    @BeforeEach
    void setUp() {

        // =========================
        // USERS
        // =========================

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

        // =========================
        // AUTHORITIES
        // =========================

        authority = new Authority();
        authority.setId(10L);
        authority.setName("Road Authority");

        anotherAuthority = new Authority();
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

        // =========================
        // PROBLEMS
        // =========================

        citizenProblem = new Problem();
        citizenProblem.setId(1L);
        citizenProblem.setTitle("Pothole");
        citizenProblem.setUser(citizen);

        anotherProblem = new Problem();
        anotherProblem.setId(2L);
        anotherProblem.setTitle("Garbage");
        anotherProblem.setUser(anotherCitizen);
    }

    // =========================================================
    // CITIZEN
    // =========================================================

    @Test
    void citizenCanViewOwnProblemHistory() {

        StatusHistory history = createHistory(
                100L,
                "OPEN",
                "VALIDATED",
                citizen
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(1L))
                .thenReturn(List.of(history));

        List<StatusHistoryResponse> result =
                statusHistoryService.getStatusHistory(
                        1L,
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        StatusHistoryResponse response = result.get(0);

        assertEquals(100L, response.getId());
        assertEquals("OPEN", response.getOldStatus().name());
        assertEquals("VALIDATED", response.getNewStatus().name());
        assertEquals("citizen@gmail.com",
                response.getChangedBy());

        verify(statusHistoryRepository)
                .findByProblemIdOrderByChangedAtAsc(1L);
    }

    @Test
    void citizenCannotViewAnotherCitizensHistory() {

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> statusHistoryService
                                .getStatusHistory(
                                        2L,
                                        "citizen@gmail.com"
                                )
                );

        assertEquals(
                403,
                exception.getStatusCode().value()
        );

        verify(statusHistoryRepository, never())
                .findByProblemIdOrderByChangedAtAsc(anyLong());
    }

    // =========================================================
    // MODERATOR
    // =========================================================

    @Test
    void moderatorCanViewAnyProblemHistory() {

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(2L))
                .thenReturn(List.of());

        List<StatusHistoryResponse> result =
                statusHistoryService.getStatusHistory(
                        2L,
                        "moderator@gmail.com"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(statusHistoryRepository)
                .findByProblemIdOrderByChangedAtAsc(2L);
    }

    // =========================================================
    // SUPER ADMIN
    // =========================================================

    @Test
    void superAdminCanViewAnyProblemHistory() {

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(superAdmin));

        when(statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(2L))
                .thenReturn(List.of());

        List<StatusHistoryResponse> result =
                statusHistoryService.getStatusHistory(
                        2L,
                        "admin@gmail.com"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(statusHistoryRepository)
                .findByProblemIdOrderByChangedAtAsc(2L);
    }

    // =========================================================
    // AUTHORITY
    // =========================================================

    @Test
    void assignedAuthorityCanViewProblemHistory() {

        Assignment assignment = new Assignment();
        assignment.setProblem(anotherProblem);
        assignment.setAuthority(authority);

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.findByProblemId(2L))
                .thenReturn(Optional.of(assignment));

        when(statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(2L))
                .thenReturn(List.of());

        List<StatusHistoryResponse> result =
                statusHistoryService.getStatusHistory(
                        2L,
                        "authority@gmail.com"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(assignmentRepository)
                .findByProblemId(2L);

        verify(statusHistoryRepository)
                .findByProblemIdOrderByChangedAtAsc(2L);
    }

    @Test
    void authorityCannotViewAnotherAuthoritiesHistory() {

        Assignment assignment = new Assignment();
        assignment.setProblem(anotherProblem);
        assignment.setAuthority(authority);

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail(
                "anotherauthority@gmail.com"))
                .thenReturn(Optional.of(anotherAuthorityUser));

        when(assignmentRepository.findByProblemId(2L))
                .thenReturn(Optional.of(assignment));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> statusHistoryService
                                .getStatusHistory(
                                        2L,
                                        "anotherauthority@gmail.com"
                                )
                );

        assertEquals(
                403,
                exception.getStatusCode().value()
        );

        verify(statusHistoryRepository, never())
                .findByProblemIdOrderByChangedAtAsc(anyLong());
    }

    @Test
    void authorityWithoutLinkedAuthorityCannotViewHistory() {

        User unlinkedAuthority = new User();
        unlinkedAuthority.setId(7L);
        unlinkedAuthority.setEmail("unlinked@gmail.com");
        unlinkedAuthority.setRole(Role.AUTHORITY);
        unlinkedAuthority.setAuthority(null);

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("unlinked@gmail.com"))
                .thenReturn(Optional.of(unlinkedAuthority));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> statusHistoryService
                                .getStatusHistory(
                                        2L,
                                        "unlinked@gmail.com"
                                )
                );

        assertEquals(
                403,
                exception.getStatusCode().value()
        );

        verify(assignmentRepository, never())
                .findByProblemId(anyLong());
    }

    @Test
    void authorityCannotViewHistoryWhenProblemIsNotAssigned() {

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.findByProblemId(2L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> statusHistoryService
                                .getStatusHistory(
                                        2L,
                                        "authority@gmail.com"
                                )
                );

        assertEquals(
                403,
                exception.getStatusCode().value()
        );

        verify(statusHistoryRepository, never())
                .findByProblemIdOrderByChangedAtAsc(anyLong());
    }

    // =========================================================
    // PROBLEM NOT FOUND
    // =========================================================

    @Test
    void getHistoryFailsWhenProblemDoesNotExist() {

        when(problemRepository.findById(999L))
                .thenReturn(Optional.empty());

        ProblemNotFoundException exception =
                assertThrows(
                        ProblemNotFoundException.class,
                        () -> statusHistoryService
                                .getStatusHistory(
                                        999L,
                                        "citizen@gmail.com"
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains("Problem not found")
        );

        verify(userRepository, never())
                .findByEmail(anyString());

        verify(statusHistoryRepository, never())
                .findByProblemIdOrderByChangedAtAsc(anyLong());
    }

    // =========================================================
    // USER NOT FOUND
    // =========================================================

    @Test
    void getHistoryFailsWhenUserDoesNotExist() {

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> statusHistoryService
                                .getStatusHistory(
                                        1L,
                                        "unknown@gmail.com"
                                )
                );

        assertEquals(
                401,
                exception.getStatusCode().value()
        );

        verify(statusHistoryRepository, never())
                .findByProblemIdOrderByChangedAtAsc(anyLong());
    }

    // =========================================================
    // EMPTY HISTORY
    // =========================================================

    @Test
    void returnsEmptyListWhenProblemHasNoHistory() {

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(1L))
                .thenReturn(List.of());

        List<StatusHistoryResponse> result =
                statusHistoryService.getStatusHistory(
                        1L,
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =========================================================
    // MULTIPLE HISTORY RECORDS
    // =========================================================

    @Test
    void returnsAllHistoryRecordsInRepositoryOrder() {

        StatusHistory first = createHistory(
                1L,
                "OPEN",
                "VALIDATED",
                moderator
        );

        StatusHistory second = createHistory(
                2L,
                "VALIDATED",
                "ASSIGNED",
                moderator
        );

        StatusHistory third = createHistory(
                3L,
                "ASSIGNED",
                "IN_PROGRESS",
                authorityUser
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(1L))
                .thenReturn(List.of(first, second, third));

        List<StatusHistoryResponse> result =
                statusHistoryService.getStatusHistory(
                        1L,
                        "citizen@gmail.com"
                );

        assertEquals(3, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());

        assertEquals("OPEN",
                result.get(0).getOldStatus().name());

        assertEquals("VALIDATED",
                result.get(0).getNewStatus().name());

        assertEquals("VALIDATED",
                result.get(1).getOldStatus().name());

        assertEquals("ASSIGNED",
                result.get(1).getNewStatus().name());

        assertEquals("ASSIGNED",
                result.get(2).getOldStatus().name());

        assertEquals("IN_PROGRESS",
                result.get(2).getNewStatus().name());
    }

    // =========================================================
    // NULL changedBy
    // =========================================================

    @Test
    void convertsHistoryWithNullChangedBy() {

        StatusHistory history = createHistory(
                500L,
                "OPEN",
                "VALIDATED",
                null
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(1L))
                .thenReturn(List.of(history));

        List<StatusHistoryResponse> result =
                statusHistoryService.getStatusHistory(
                        1L,
                        "citizen@gmail.com"
                );

        assertEquals(1, result.size());

        StatusHistoryResponse response = result.get(0);

        assertEquals(500L, response.getId());
        assertEquals("OPEN",
                response.getOldStatus().name());
        assertEquals("VALIDATED",
                response.getNewStatus().name());

        assertNull(response.getChangedBy());
    }

    // =========================================================
    // RESPONSE MAPPING
    // =========================================================

    @Test
    void responseContainsChangedByEmail() {

        StatusHistory history = createHistory(
                600L,
                "RESOLVED",
                "CLOSED",
                authorityUser
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(1L))
                .thenReturn(List.of(history));

        List<StatusHistoryResponse> result =
                statusHistoryService.getStatusHistory(
                        1L,
                        "citizen@gmail.com"
                );

        assertEquals(
                "authority@gmail.com",
                result.get(0).getChangedBy()
        );
    }

    // =========================================================
    // HELPER
    // =========================================================

    private StatusHistory createHistory(
            Long id,
            String oldStatus,
            String newStatus,
            User changedBy) {

        StatusHistory history = new StatusHistory();

        history.setId(id);

        history.setOldStatus(
                com.localproblemsolver.entity.ProblemStatus
                        .valueOf(oldStatus)
        );

        history.setNewStatus(
                com.localproblemsolver.entity.ProblemStatus
                        .valueOf(newStatus)
        );

        history.setChangedBy(changedBy);

        history.setChangedAt(LocalDateTime.now());

        return history;
    }
}
package com.localproblemsolver.service;

import com.localproblemsolver.dto.analytics.*;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.AuthorityRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.SlaRepository;
import com.localproblemsolver.repository.StatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SlaRepository slaRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private StatusHistoryRepository statusHistoryRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Authority authority1;
    private Authority authority2;

    @BeforeEach
    void setUp() {

        authority1 = new Authority();
        authority1.setId(1L);
        authority1.setName("Road Authority");

        authority2 = new Authority();
        authority2.setId(2L);
        authority2.setName("Water Authority");
    }

    // =========================================================
    // OVERVIEW
    // =========================================================

    @Test
    void getOverviewReturnsCorrectCounts() {

        when(problemRepository.count())
                .thenReturn(100L);

        when(problemRepository.countByStatusIn(anyList()))
                .thenReturn(60L);

        when(problemRepository.countByStatus(ProblemStatus.RESOLVED))
                .thenReturn(15L);

        when(problemRepository.countByStatus(ProblemStatus.CLOSED))
                .thenReturn(10L);

        when(problemRepository.countByStatus(ProblemStatus.REJECTED))
                .thenReturn(5L);

        when(problemRepository.countByStatus(ProblemStatus.DUPLICATE))
                .thenReturn(7L);

        when(problemRepository.countByStatus(ProblemStatus.REOPENED))
                .thenReturn(3L);

        AnalyticsOverviewResponse result =
                analyticsService.getOverview();

        assertNotNull(result);
        assertEquals(100L, result.getTotalProblems());
        assertEquals(60L, result.getActiveProblems());
        assertEquals(15L, result.getResolvedProblems());
        assertEquals(10L, result.getClosedProblems());
        assertEquals(5L, result.getRejectedProblems());
        assertEquals(7L, result.getDuplicateProblems());
        assertEquals(3L, result.getReopenedProblems());
    }

    // =========================================================
    // CATEGORY ANALYTICS
    // =========================================================

    @Test
    void getCategoryAnalyticsReturnsCorrectData() {

        when(problemRepository.countProblemsByCategory())
                .thenReturn(List.<Object[]>of(
                        new Object[]{"Road", 20L},
                        new Object[]{"Water", 15L}
                ));

        List<CategoryAnalyticsResponse> result =
                analyticsService.getCategoryAnalytics();

        assertEquals(2, result.size());

        assertEquals(
                "Road",
                result.get(0).getCategory()
        );

        assertEquals(
                20L,
                result.get(0).getCount()
        );

        assertEquals(
                "Water",
                result.get(1).getCategory()
        );

        assertEquals(
                15L,
                result.get(1).getCount()
        );
    }

    @Test
    void getCategoryAnalyticsReturnsEmptyList() {

        when(problemRepository.countProblemsByCategory())
                .thenReturn(List.<Object[]>of());

        List<CategoryAnalyticsResponse> result =
                analyticsService.getCategoryAnalytics();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =========================================================
    // SEVERITY ANALYTICS
    // =========================================================

    @Test
    void getSeverityAnalyticsReturnsCorrectData() {

        when(problemRepository.countProblemsBySeverity())
                .thenReturn(List.<Object[]>of(
                        new Object[]{"HIGH", 30L},
                        new Object[]{"LOW", 10L}
                ));

        List<SeverityAnalyticsResponse> result =
                analyticsService.getSeverityAnalytics();

        assertEquals(2, result.size());

        assertEquals(
                "HIGH",
                result.get(0).getSeverity()
        );

        assertEquals(
                30L,
                result.get(0).getCount()
        );

        assertEquals(
                "LOW",
                result.get(1).getSeverity()
        );
    }

    // =========================================================
    // PRIORITY ANALYTICS
    // =========================================================

    @Test
    void getPriorityAnalyticsReturnsCorrectData() {

        when(problemRepository.countProblemsByPriority())
                .thenReturn(List.<Object[]>of(
                        new Object[]{"CRITICAL", 5L},
                        new Object[]{"MEDIUM", 25L}
                ));

        List<PriorityAnalyticsResponse> result =
                analyticsService.getPriorityAnalytics();

        assertEquals(2, result.size());

        assertEquals(
                "CRITICAL",
                result.get(0).getPriority()
        );

        assertEquals(
                5L,
                result.get(0).getCount()
        );

        assertEquals(
                "MEDIUM",
                result.get(1).getPriority()
        );
    }

    // =========================================================
    // TREND ANALYTICS
    // =========================================================

    @Test
    void getTrendAnalyticsReturnsEveryDateInRange() {

        LocalDate from =
                LocalDate.of(2026, 1, 1);

        LocalDate to =
                LocalDate.of(2026, 1, 3);

        when(problemRepository.countProblemsByDateRange(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.<Object[]>of(
                new Object[]{
                        Date.valueOf("2026-01-01"),
                        5L
                },
                new Object[]{
                        LocalDate.of(2026, 1, 3),
                        10L
                }
        ));

        List<TrendAnalyticsResponse> result =
                analyticsService.getTrendAnalytics(from, to);

        assertEquals(3, result.size());

        assertEquals(
                LocalDate.of(2026, 1, 1),
                result.get(0).getDate()
        );

        assertEquals(
                5L,
                result.get(0).getCount()
        );

        assertEquals(
                LocalDate.of(2026, 1, 2),
                result.get(1).getDate()
        );

        assertEquals(
                0L,
                result.get(1).getCount()
        );

        assertEquals(
                LocalDate.of(2026, 1, 3),
                result.get(2).getDate()
        );

        assertEquals(
                10L,
                result.get(2).getCount()
        );
    }

    @Test
    void getTrendAnalyticsAcceptsLocalDateResult() {

        LocalDate date =
                LocalDate.of(2026, 2, 1);

        when(problemRepository.countProblemsByDateRange(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.<Object[]>of(
                new Object[]{date, 8L}
        ));

        List<TrendAnalyticsResponse> result =
                analyticsService.getTrendAnalytics(
                        date,
                        date
                );

        assertEquals(1, result.size());
        assertEquals(8L, result.get(0).getCount());
    }

    @Test
    void getTrendAnalyticsAcceptsStringDateResult() {

        LocalDate date =
                LocalDate.of(2026, 3, 1);

        when(problemRepository.countProblemsByDateRange(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.<Object[]>of(
                new Object[]{"2026-03-01", 12L}
        ));

        List<TrendAnalyticsResponse> result =
                analyticsService.getTrendAnalytics(
                        date,
                        date
                );

        assertEquals(1, result.size());
        assertEquals(12L, result.get(0).getCount());
    }

    @Test
    void getTrendAnalyticsFailsWhenFromIsNull() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> analyticsService.getTrendAnalytics(
                                null,
                                LocalDate.now()
                        )
                );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );
    }

    @Test
    void getTrendAnalyticsFailsWhenToIsNull() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> analyticsService.getTrendAnalytics(
                                LocalDate.now(),
                                null
                        )
                );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );
    }

    @Test
    void getTrendAnalyticsFailsWhenFromAfterTo() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> analyticsService.getTrendAnalytics(
                                LocalDate.of(2026, 5, 10),
                                LocalDate.of(2026, 5, 1)
                        )
                );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );

        verify(
                problemRepository,
                never()
        ).countProblemsByDateRange(
                any(),
                any()
        );
    }

    // =========================================================
    // SLA ANALYTICS
    // =========================================================

    @Test
    void getSlaAnalyticsCalculatesCompliance() {

        when(slaRepository.count())
                .thenReturn(100L);

        when(slaRepository.countByBreachedTrue())
                .thenReturn(20L);

        SlaAnalyticsResponse result =
                analyticsService.getSlaAnalytics();

        assertEquals(100L, result.getTotalSlas());
        assertEquals(20L, result.getBreachedSlas());
        assertEquals(80L, result.getWithinSlas());

        assertEquals(
                80.0,
                result.getCompliancePercentage()
        );
    }

    @Test
    void getSlaAnalyticsReturnsZeroComplianceWhenNoSlas() {

        when(slaRepository.count())
                .thenReturn(0L);

        when(slaRepository.countByBreachedTrue())
                .thenReturn(0L);

        SlaAnalyticsResponse result =
                analyticsService.getSlaAnalytics();

        assertEquals(0L, result.getTotalSlas());
        assertEquals(0L, result.getBreachedSlas());
        assertEquals(0L, result.getWithinSlas());

        assertEquals(
                0.0,
                result.getCompliancePercentage()
        );
    }

    // =========================================================
    // AUTHORITY ANALYTICS
    // =========================================================

    @Test
    void getAuthorityAnalyticsReturnsStatistics() {

        when(authorityRepository.findAllByOrderByIdAsc())
                .thenReturn(
                        List.of(authority1)
                );

        when(assignmentRepository.getAuthorityAnalytics())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                1L,
                                "Road Authority",
                                20L,
                                8L,
                                7L,
                                5L,
                                2L
                        }
                ));

        List<AuthorityAnalyticsResponse> result =
                analyticsService.getAuthorityAnalytics();

        assertEquals(1, result.size());

        AuthorityAnalyticsResponse response =
                result.get(0);

        assertEquals(
                1L,
                response.getAuthorityId()
        );

        assertEquals(
                "Road Authority",
                response.getAuthorityName()
        );

        assertEquals(
                20L,
                response.getAssignedProblems()
        );

        assertEquals(
                8L,
                response.getActiveProblems()
        );

        assertEquals(
                7L,
                response.getResolvedProblems()
        );

        assertEquals(
                5L,
                response.getClosedProblems()
        );

        assertEquals(
                2L,
                response.getSlaBreachedProblems()
        );
    }

    @Test
    void getAuthorityAnalyticsUsesZeroWhenStatisticsMissing() {

        when(authorityRepository.findAllByOrderByIdAsc())
                .thenReturn(
                        List.of(authority1)
                );

        when(assignmentRepository.getAuthorityAnalytics())
                .thenReturn(List.<Object[]>of());

        List<AuthorityAnalyticsResponse> result =
                analyticsService.getAuthorityAnalytics();

        assertEquals(1, result.size());

        AuthorityAnalyticsResponse response =
                result.get(0);

        assertEquals(
                0L,
                response.getAssignedProblems()
        );

        assertEquals(
                0L,
                response.getActiveProblems()
        );

        assertEquals(
                0L,
                response.getResolvedProblems()
        );

        assertEquals(
                0L,
                response.getClosedProblems()
        );

        assertEquals(
                0L,
                response.getSlaBreachedProblems()
        );
    }

    @Test
    void getAuthorityAnalyticsReturnsEmptyWhenNoAuthorities() {

        when(authorityRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of());

        when(assignmentRepository.getAuthorityAnalytics())
                .thenReturn(List.<Object[]>of());

        List<AuthorityAnalyticsResponse> result =
                analyticsService.getAuthorityAnalytics();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =========================================================
    // RESOLUTION ANALYTICS
    // =========================================================

    @Test
    void getResolutionAnalyticsCalculatesRate() {

        when(problemRepository.count())
                .thenReturn(100L);

        when(problemRepository.countByStatus(
                ProblemStatus.RESOLVED))
                .thenReturn(30L);

        when(problemRepository.countByStatus(
                ProblemStatus.CLOSED))
                .thenReturn(50L);

        when(problemRepository.countByStatus(
                ProblemStatus.REOPENED))
                .thenReturn(10L);

        ResolutionAnalyticsResponse result =
                analyticsService.getResolutionAnalytics();

        assertEquals(
                100L,
                result.getTotalProblems()
        );

        assertEquals(
                30L,
                result.getResolvedProblems()
        );

        assertEquals(
                50L,
                result.getClosedProblems()
        );

        assertEquals(
                10L,
                result.getReopenedProblems()
        );

        assertEquals(
                50.0,
                result.getResolutionRate()
        );
    }

    @Test
    void getResolutionAnalyticsReturnsZeroRateWhenNoProblems() {

        when(problemRepository.count())
                .thenReturn(0L);

        when(problemRepository.countByStatus(
                ProblemStatus.RESOLVED))
                .thenReturn(0L);

        when(problemRepository.countByStatus(
                ProblemStatus.CLOSED))
                .thenReturn(0L);

        when(problemRepository.countByStatus(
                ProblemStatus.REOPENED))
                .thenReturn(0L);

        ResolutionAnalyticsResponse result =
                analyticsService.getResolutionAnalytics();

        assertEquals(
                0.0,
                result.getResolutionRate()
        );
    }

    // =========================================================
    // LOCATION ANALYTICS
    // =========================================================

    @Test
    void getLocationAnalyticsReturnsCorrectData() {

        when(problemRepository.countProblemsByLocation())
                .thenReturn(List.<Object[]>of(
                        new Object[]{"Nashik", 25L},
                        new Object[]{"Pune", 15L}
                ));

        List<LocationAnalyticsResponse> result =
                analyticsService.getLocationAnalytics();

        assertEquals(2, result.size());

        assertEquals(
                "Nashik",
                result.get(0).getLocation()
        );

        assertEquals(
                25L,
                result.get(0).getProblemCount()
        );

        assertEquals(
                "Pune",
                result.get(1).getLocation()
        );
    }

    // =========================================================
    // STATUS TRANSITION ANALYTICS
    // =========================================================

    @Test
    void getStatusTransitionAnalyticsReturnsCorrectData() {

        when(statusHistoryRepository.countStatusTransitions())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                "OPEN",
                                "VALIDATED",
                                20L
                        },
                        new Object[]{
                                "VALIDATED",
                                "ASSIGNED",
                                15L
                        }
                ));

        List<StatusTransitionAnalyticsResponse> result =
                analyticsService.getStatusTransitionAnalytics();

        assertEquals(2, result.size());

        assertEquals(
                "OPEN",
                result.get(0).getFromStatus()
        );

        assertEquals(
                "VALIDATED",
                result.get(0).getToStatus()
        );

        assertEquals(
                20L,
                result.get(0).getTransitionCount()
        );

        assertEquals(
                "VALIDATED",
                result.get(1).getFromStatus()
        );
    }

    // =========================================================
    // GEOGRAPHIC ANALYTICS
    // =========================================================

    @Test
    void getGeographicAnalyticsReturnsCorrectData() {

        when(problemRepository
                .findProblemsForGeographicAnalytics())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                1L,
                                "Nashik",
                                20.0,
                                73.8,
                                "Road",
                                "HIGH",
                                "CRITICAL",
                                "IN_PROGRESS"
                        }
                ));

        List<GeographicAnalyticsResponse> result =
                analyticsService.getGeographicAnalytics();

        assertEquals(1, result.size());

        GeographicAnalyticsResponse response =
                result.get(0);

        assertEquals(
                1L,
                response.getProblemId()
        );

        assertEquals(
                "Nashik",
                response.getLocation()
        );

        assertEquals(
                20.0,
                response.getLatitude()
        );

        assertEquals(
                73.8,
                response.getLongitude()
        );

        assertEquals(
                "Road",
                response.getCategory()
        );

        assertEquals(
                "HIGH",
                response.getSeverity()
        );

        assertEquals(
                "CRITICAL",
                response.getPriority()
        );

        assertEquals(
                "IN_PROGRESS",
                response.getStatus()
        );
    }

    @Test
    void getGeographicAnalyticsAllowsNullOptionalFields() {

        when(problemRepository
                .findProblemsForGeographicAnalytics())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                2L,
                                "Pune",
                                18.5,
                                73.8,
                                null,
                                null,
                                null,
                                null
                        }
                ));

        List<GeographicAnalyticsResponse> result =
                analyticsService.getGeographicAnalytics();

        assertEquals(1, result.size());

        GeographicAnalyticsResponse response =
                result.get(0);

        assertNull(response.getCategory());
        assertNull(response.getSeverity());
        assertNull(response.getPriority());
        assertNull(response.getStatus());
    }

    // =========================================================
    // LIFECYCLE ANALYTICS
    // =========================================================

    @Test
    void getLifecycleAnalyticsCalculatesAverageDurations() {

        LocalDateTime created =
                LocalDateTime.of(
                        2026, 1, 1, 8, 0
                );

        LocalDateTime validated =
                LocalDateTime.of(
                        2026, 1, 1, 10, 0
                );

        LocalDateTime assigned =
                LocalDateTime.of(
                        2026, 1, 1, 14, 0
                );

        LocalDateTime inProgress =
                LocalDateTime.of(
                        2026, 1, 1, 18, 0
                );

        LocalDateTime resolved =
                LocalDateTime.of(
                        2026, 1, 2, 6, 0
                );

        LocalDateTime closed =
                LocalDateTime.of(
                        2026, 1, 2, 8, 0
                );

        when(statusHistoryRepository
                .findAllStatusHistoryForLifecycle())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                1L,
                                ProblemStatus.OPEN,
                                ProblemStatus.VALIDATED,
                                validated,
                                created
                        },
                        new Object[]{
                                1L,
                                ProblemStatus.VALIDATED,
                                ProblemStatus.ASSIGNED,
                                assigned,
                                created
                        },
                        new Object[]{
                                1L,
                                ProblemStatus.ASSIGNED,
                                ProblemStatus.IN_PROGRESS,
                                inProgress,
                                created
                        },
                        new Object[]{
                                1L,
                                ProblemStatus.IN_PROGRESS,
                                ProblemStatus.RESOLVED,
                                resolved,
                                created
                        },
                        new Object[]{
                                1L,
                                ProblemStatus.RESOLVED,
                                ProblemStatus.CLOSED,
                                closed,
                                created
                        }
                ));

        LifecycleAnalyticsResponse result =
                analyticsService.getLifecycleAnalytics();

        assertEquals(
                2.0,
                result.getAverageOpenToValidatedHours()
        );

        assertEquals(
                4.0,
                result.getAverageValidatedToAssignedHours()
        );

        assertEquals(
                4.0,
                result.getAverageAssignedToInProgressHours()
        );

        assertEquals(
                12.0,
                result.getAverageInProgressToResolvedHours()
        );

        assertEquals(
                2.0,
                result.getAverageResolvedToClosedHours()
        );
    }

    @Test
    void getLifecycleAnalyticsReturnsZeroWhenNoHistoryExists() {

        when(statusHistoryRepository
                .findAllStatusHistoryForLifecycle())
                .thenReturn(List.<Object[]>of());

        LifecycleAnalyticsResponse result =
                analyticsService.getLifecycleAnalytics();

        assertEquals(
                0.0,
                result.getAverageOpenToValidatedHours()
        );

        assertEquals(
                0.0,
                result.getAverageValidatedToAssignedHours()
        );

        assertEquals(
                0.0,
                result.getAverageAssignedToInProgressHours()
        );

        assertEquals(
                0.0,
                result.getAverageInProgressToResolvedHours()
        );

        assertEquals(
                0.0,
                result.getAverageResolvedToClosedHours()
        );
    }

    @Test
    void getLifecycleAnalyticsIgnoresNegativeDuration() {

        LocalDateTime created =
                LocalDateTime.of(
                        2026, 1, 1, 10, 0
                );

        LocalDateTime changed =
                LocalDateTime.of(
                        2026, 1, 1, 8, 0
                );

        when(statusHistoryRepository
                .findAllStatusHistoryForLifecycle())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                1L,
                                ProblemStatus.OPEN,
                                ProblemStatus.VALIDATED,
                                changed,
                                created
                        }
                ));

        LifecycleAnalyticsResponse result =
                analyticsService.getLifecycleAnalytics();

        assertEquals(
                0.0,
                result.getAverageOpenToValidatedHours()
        );
    }

    @Test
    void getLifecycleAnalyticsIgnoresNullTimes() {

        when(statusHistoryRepository
                .findAllStatusHistoryForLifecycle())
                .thenReturn(List.<Object[]>of(
                        new Object[]{
                                1L,
                                ProblemStatus.OPEN,
                                ProblemStatus.VALIDATED,
                                null,
                                LocalDateTime.now()
                        }
                ));

        LifecycleAnalyticsResponse result =
                analyticsService.getLifecycleAnalytics();

        assertEquals(
                0.0,
                result.getAverageOpenToValidatedHours()
        );
    }
}
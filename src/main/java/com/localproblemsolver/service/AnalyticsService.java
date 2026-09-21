package com.localproblemsolver.service;

import com.localproblemsolver.dto.analytics.AnalyticsOverviewResponse;
import com.localproblemsolver.dto.analytics.AuthorityAnalyticsResponse;
import com.localproblemsolver.dto.analytics.CategoryAnalyticsResponse;
import com.localproblemsolver.dto.analytics.GeographicAnalyticsResponse;
import com.localproblemsolver.dto.analytics.LifecycleAnalyticsResponse;
import com.localproblemsolver.dto.analytics.LocationAnalyticsResponse;
import com.localproblemsolver.dto.analytics.PriorityAnalyticsResponse;
import com.localproblemsolver.dto.analytics.ResolutionAnalyticsResponse;
import com.localproblemsolver.dto.analytics.SeverityAnalyticsResponse;
import com.localproblemsolver.dto.analytics.SlaAnalyticsResponse;
import com.localproblemsolver.dto.analytics.StatusTransitionAnalyticsResponse;
import com.localproblemsolver.dto.analytics.TrendAnalyticsResponse;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.AuthorityRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.SlaRepository;
import com.localproblemsolver.repository.StatusHistoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final ProblemRepository problemRepository;
    private final SlaRepository slaRepository;
    private final AssignmentRepository assignmentRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final AuthorityRepository authorityRepository;

    public AnalyticsService(
            ProblemRepository problemRepository,
            SlaRepository slaRepository,
            AssignmentRepository assignmentRepository,
            StatusHistoryRepository statusHistoryRepository,
            AuthorityRepository authorityRepository) {

        this.problemRepository = problemRepository;
        this.slaRepository = slaRepository;
        this.assignmentRepository = assignmentRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.authorityRepository = authorityRepository;
    }

    public AnalyticsOverviewResponse getOverview() {

        long totalProblems = problemRepository.count();

        long activeProblems =
                problemRepository.countByStatusIn(
                        List.of(
                                ProblemStatus.OPEN,
                                ProblemStatus.VALIDATED,
                                ProblemStatus.ASSIGNED,
                                ProblemStatus.IN_PROGRESS,
                                ProblemStatus.REOPENED
                        )
                );

        long resolvedProblems =
                problemRepository.countByStatus(
                        ProblemStatus.RESOLVED
                );

        long closedProblems =
                problemRepository.countByStatus(
                        ProblemStatus.CLOSED
                );

        long rejectedProblems =
                problemRepository.countByStatus(
                        ProblemStatus.REJECTED
                );

        long duplicateProblems =
                problemRepository.countByStatus(
                        ProblemStatus.DUPLICATE
                );

        long reopenedProblems =
                problemRepository.countByStatus(
                        ProblemStatus.REOPENED
                );

        return new AnalyticsOverviewResponse(
                totalProblems,
                activeProblems,
                resolvedProblems,
                closedProblems,
                rejectedProblems,
                duplicateProblems,
                reopenedProblems
        );
    }

    public List<CategoryAnalyticsResponse> getCategoryAnalytics() {

        List<Object[]> results =
                problemRepository.countProblemsByCategory();

        List<CategoryAnalyticsResponse> response =
                new ArrayList<>();

        for (Object[] result : results) {

            String category = (String) result[0];

            long count =
                    ((Number) result[1]).longValue();

            response.add(
                    new CategoryAnalyticsResponse(
                            category,
                            count
                    )
            );
        }

        return response;
    }

    public List<SeverityAnalyticsResponse> getSeverityAnalytics() {

        List<Object[]> results =
                problemRepository.countProblemsBySeverity();

        List<SeverityAnalyticsResponse> response =
                new ArrayList<>();

        for (Object[] result : results) {

            String severity =
                    result[0].toString();

            long count =
                    ((Number) result[1]).longValue();

            response.add(
                    new SeverityAnalyticsResponse(
                            severity,
                            count
                    )
            );
        }

        return response;
    }

    public List<PriorityAnalyticsResponse> getPriorityAnalytics() {

        List<Object[]> results =
                problemRepository.countProblemsByPriority();

        List<PriorityAnalyticsResponse> response =
                new ArrayList<>();

        for (Object[] result : results) {

            String priority =
                    result[0].toString();

            long count =
                    ((Number) result[1]).longValue();

            response.add(
                    new PriorityAnalyticsResponse(
                            priority,
                            count
                    )
            );
        }

        return response;
    }

    public List<TrendAnalyticsResponse> getTrendAnalytics(
            LocalDate from,
            LocalDate to) {

        if (from == null || to == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Both from and to dates are required"
            );
        }

        if (from.isAfter(to)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "From date cannot be after to date"
            );
        }

        LocalDateTime fromDateTime =
                from.atStartOfDay();

        LocalDateTime toDateTime =
                to.plusDays(1).atStartOfDay();

        List<Object[]> results =
                problemRepository.countProblemsByDateRange(
                        fromDateTime,
                        toDateTime
                );

        Map<LocalDate, Long> countByDate =
                new HashMap<>();

        for (Object[] result : results) {

            LocalDate date;

            if (result[0] instanceof java.sql.Date sqlDate) {

                date = sqlDate.toLocalDate();

            } else if (result[0] instanceof LocalDate localDate) {

                date = localDate;

            } else {

                date = LocalDate.parse(
                        result[0].toString()
                );
            }

            long count =
                    ((Number) result[1]).longValue();

            countByDate.put(
                    date,
                    count
            );
        }

        List<TrendAnalyticsResponse> response =
                new ArrayList<>();

        LocalDate currentDate = from;

        while (!currentDate.isAfter(to)) {

            long count =
                    countByDate.getOrDefault(
                            currentDate,
                            0L
                    );

            response.add(
                    new TrendAnalyticsResponse(
                            currentDate,
                            count
                    )
            );

            currentDate =
                    currentDate.plusDays(1);
        }

        return response;
    }

    public SlaAnalyticsResponse getSlaAnalytics() {

        long totalSlas =
                slaRepository.count();

        long breachedSlas =
                slaRepository.countByBreachedTrue();

        long withinSlas =
                totalSlas - breachedSlas;

        double compliancePercentage =
                0.0;

        if (totalSlas > 0) {

            compliancePercentage =
                    ((double) withinSlas / totalSlas) * 100;
        }

        return new SlaAnalyticsResponse(
                totalSlas,
                breachedSlas,
                withinSlas,
                compliancePercentage
        );
    }

    public List<AuthorityAnalyticsResponse> getAuthorityAnalytics() {

        List<Authority> authorities =
                authorityRepository.findAllByOrderByIdAsc();

        List<Object[]> results =
                assignmentRepository.getAuthorityAnalytics();

        Map<Long, Object[]> statisticsByAuthority =
                new HashMap<>();

        for (Object[] result : results) {

            Long authorityId =
                    ((Number) result[0]).longValue();

            statisticsByAuthority.put(
                    authorityId,
                    result
            );
        }

        List<AuthorityAnalyticsResponse> response =
                new ArrayList<>();

        for (Authority authority : authorities) {

            Long authorityId =
                    authority.getId();

            Object[] statistics =
                    statisticsByAuthority.get(authorityId);

            long assignedProblems = 0;
            long activeProblems = 0;
            long resolvedProblems = 0;
            long closedProblems = 0;
            long slaBreachedProblems = 0;

            if (statistics != null) {

                assignedProblems =
                        ((Number) statistics[2]).longValue();

                activeProblems =
                        ((Number) statistics[3]).longValue();

                resolvedProblems =
                        ((Number) statistics[4]).longValue();

                closedProblems =
                        ((Number) statistics[5]).longValue();

                slaBreachedProblems =
                        ((Number) statistics[6]).longValue();
            }

            response.add(
                    new AuthorityAnalyticsResponse(
                            authorityId,
                            authority.getName(),
                            assignedProblems,
                            activeProblems,
                            resolvedProblems,
                            closedProblems,
                            slaBreachedProblems
                    )
            );
        }

        return response;
    }

    public ResolutionAnalyticsResponse getResolutionAnalytics() {

        long totalProblems =
                problemRepository.count();

        long resolvedProblems =
                problemRepository.countByStatus(
                        ProblemStatus.RESOLVED
                );

        long closedProblems =
                problemRepository.countByStatus(
                        ProblemStatus.CLOSED
                );

        long reopenedProblems =
                problemRepository.countByStatus(
                        ProblemStatus.REOPENED
                );

        double resolutionRate =
                0.0;

        if (totalProblems > 0) {

            resolutionRate =
                    ((double) closedProblems / totalProblems) * 100;
        }

        return new ResolutionAnalyticsResponse(
                totalProblems,
                resolvedProblems,
                closedProblems,
                reopenedProblems,
                resolutionRate
        );
    }

    public List<LocationAnalyticsResponse> getLocationAnalytics() {

        List<Object[]> results =
                problemRepository.countProblemsByLocation();

        List<LocationAnalyticsResponse> response =
                new ArrayList<>();

        for (Object[] result : results) {

            String location =
                    (String) result[0];

            long problemCount =
                    ((Number) result[1]).longValue();

            response.add(
                    new LocationAnalyticsResponse(
                            location,
                            problemCount
                    )
            );
        }

        return response;
    }

    public List<StatusTransitionAnalyticsResponse>
    getStatusTransitionAnalytics() {

        List<Object[]> results =
                statusHistoryRepository.countStatusTransitions();

        List<StatusTransitionAnalyticsResponse> response =
                new ArrayList<>();

        for (Object[] result : results) {

            String fromStatus =
                    result[0].toString();

            String toStatus =
                    result[1].toString();

            long transitionCount =
                    ((Number) result[2]).longValue();

            response.add(
                    new StatusTransitionAnalyticsResponse(
                            fromStatus,
                            toStatus,
                            transitionCount
                    )
            );
        }

        return response;
    }

    public List<GeographicAnalyticsResponse>
    getGeographicAnalytics() {

        List<Object[]> results =
                problemRepository.findProblemsForGeographicAnalytics();

        List<GeographicAnalyticsResponse> response =
                new ArrayList<>();

        for (Object[] result : results) {

            Long problemId =
                    ((Number) result[0]).longValue();

            String location =
                    (String) result[1];

            Double latitude =
                    ((Number) result[2]).doubleValue();

            Double longitude =
                    ((Number) result[3]).doubleValue();

            String category =
                    result[4] != null
                            ? result[4].toString()
                            : null;

            String severity =
                    result[5] != null
                            ? result[5].toString()
                            : null;

            String priority =
                    result[6] != null
                            ? result[6].toString()
                            : null;

            String status =
                    result[7] != null
                            ? result[7].toString()
                            : null;

            response.add(
                    new GeographicAnalyticsResponse(
                            problemId,
                            location,
                            latitude,
                            longitude,
                            category,
                            severity,
                            priority,
                            status
                    )
            );
        }

        return response;
    }

    public LifecycleAnalyticsResponse
    getLifecycleAnalytics() {

        List<Object[]> results =
                statusHistoryRepository
                        .findAllStatusHistoryForLifecycle();

        Map<Long, List<StatusHistoryRecord>>
                historyByProblem =
                new HashMap<>();

        /*
         * Repository result order:
         *
         * 0 → problemId
         * 1 → oldStatus
         * 2 → newStatus
         * 3 → changedAt
         * 4 → problem.createdAt
         */
        for (Object[] result : results) {

            Long problemId =
                    ((Number) result[0]).longValue();

            ProblemStatus oldStatus =
                    (ProblemStatus) result[1];

            ProblemStatus newStatus =
                    (ProblemStatus) result[2];

            LocalDateTime changedAt =
                    (LocalDateTime) result[3];

            LocalDateTime createdAt =
                    (LocalDateTime) result[4];

            StatusHistoryRecord record =
                    new StatusHistoryRecord(
                            oldStatus,
                            newStatus,
                            changedAt,
                            createdAt
                    );

            historyByProblem
                    .computeIfAbsent(
                            problemId,
                            key -> new ArrayList<>()
                    )
                    .add(record);
        }

        List<Double> openToValidated =
                new ArrayList<>();

        List<Double> validatedToAssigned =
                new ArrayList<>();

        List<Double> assignedToInProgress =
                new ArrayList<>();

        List<Double> inProgressToResolved =
                new ArrayList<>();

        List<Double> resolvedToClosed =
                new ArrayList<>();

        /*
         * Process each problem independently.
         */
        for (List<StatusHistoryRecord> history :
                historyByProblem.values()) {

            for (int i = 0; i < history.size(); i++) {

                StatusHistoryRecord current =
                        history.get(i);

                LocalDateTime startTime;

                /*
                 * OPEN is the initial status.
                 *
                 * There is normally no history row representing
                 * "entered OPEN", so use Problem.createdAt.
                 */
                if (current.oldStatus() ==
                        ProblemStatus.OPEN) {

                    startTime =
                            current.createdAt();

                } else {

                    /*
                     * For every later status, the starting point
                     * is the previous status change.
                     */
                    if (i == 0) {
                        continue;
                    }

                    StatusHistoryRecord previous =
                            history.get(i - 1);

                    startTime =
                            previous.changedAt();
                }

                LocalDateTime endTime =
                        current.changedAt();

                if (startTime == null
                        || endTime == null) {

                    continue;
                }

                double hours =
                        Duration.between(
                                startTime,
                                endTime
                        ).toMinutes() / 60.0;

                /*
                 * Ignore invalid negative durations.
                 */
                if (hours < 0) {
                    continue;
                }

                /*
                 * OPEN → VALIDATED
                 */
                if (current.oldStatus() ==
                        ProblemStatus.OPEN
                        && current.newStatus() ==
                        ProblemStatus.VALIDATED) {

                    openToValidated.add(hours);
                }

                /*
                 * VALIDATED → ASSIGNED
                 */
                else if (
                        current.oldStatus() ==
                                ProblemStatus.VALIDATED
                                && current.newStatus() ==
                                ProblemStatus.ASSIGNED) {

                    validatedToAssigned.add(hours);
                }

                /*
                 * ASSIGNED → IN_PROGRESS
                 */
                else if (
                        current.oldStatus() ==
                                ProblemStatus.ASSIGNED
                                && current.newStatus() ==
                                ProblemStatus.IN_PROGRESS) {

                    assignedToInProgress.add(hours);
                }

                /*
                 * IN_PROGRESS → RESOLVED
                 */
                else if (
                        current.oldStatus() ==
                                ProblemStatus.IN_PROGRESS
                                && current.newStatus() ==
                                ProblemStatus.RESOLVED) {

                    inProgressToResolved.add(hours);
                }

                /*
                 * RESOLVED → CLOSED
                 */
                else if (
                        current.oldStatus() ==
                                ProblemStatus.RESOLVED
                                && current.newStatus() ==
                                ProblemStatus.CLOSED) {

                    resolvedToClosed.add(hours);
                }
            }
        }

        return new LifecycleAnalyticsResponse(
                average(openToValidated),
                average(validatedToAssigned),
                average(assignedToInProgress),
                average(inProgressToResolved),
                average(resolvedToClosed)
        );
    }

    private double average(
            List<Double> values) {

        if (values.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;

        for (double value : values) {
            total += value;
        }

        double result =
                total / values.size();

        /*
         * Round to two decimal places.
         */
        return Math.round(result * 100.0) / 100.0;
    }

    private record StatusHistoryRecord(
            ProblemStatus oldStatus,
            ProblemStatus newStatus,
            LocalDateTime changedAt,
            LocalDateTime createdAt) {
    }
}
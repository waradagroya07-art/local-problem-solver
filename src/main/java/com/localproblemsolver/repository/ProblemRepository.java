package com.localproblemsolver.repository;

import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    long countByStatus(ProblemStatus status);

    long countByStatusIn(Collection<ProblemStatus> statuses);

    @Query("""
        SELECT c.name, COUNT(p.id)
        FROM Problem p
        JOIN p.category c
        GROUP BY c.name
        ORDER BY COUNT(p.id) DESC
    """)
    List<Object[]> countProblemsByCategory();

    @Query("""
        SELECT p.severity, COUNT(p.id)
        FROM Problem p
        GROUP BY p.severity
        ORDER BY COUNT(p.id) DESC
    """)
    List<Object[]> countProblemsBySeverity();

    @Query("""
        SELECT p.priority, COUNT(p.id)
        FROM Problem p
        GROUP BY p.priority
        ORDER BY COUNT(p.id) DESC
    """)
    List<Object[]> countProblemsByPriority();

    @Query("""
        SELECT FUNCTION('DATE', p.createdAt), COUNT(p.id)
        FROM Problem p
        WHERE p.createdAt >= :from
          AND p.createdAt < :to
        GROUP BY FUNCTION('DATE', p.createdAt)
        ORDER BY FUNCTION('DATE', p.createdAt)
    """)
    List<Object[]> countProblemsByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT p.location, COUNT(p.id)
        FROM Problem p
        WHERE p.location IS NOT NULL
        GROUP BY p.location
        ORDER BY COUNT(p.id) DESC
    """)
    List<Object[]> countProblemsByLocation();

    @Query("""
        SELECT p.id,
               p.location,
               p.latitude,
               p.longitude,
               c.name,
               p.severity,
               p.priority,
               p.status
        FROM Problem p
        LEFT JOIN p.category c
        WHERE p.latitude IS NOT NULL
          AND p.longitude IS NOT NULL
        ORDER BY p.id
    """)
    List<Object[]> findProblemsForGeographicAnalytics();

    @Query("""
        SELECT COUNT(a.id)
        FROM Assignment a
        WHERE a.authority.id = :authorityId
          AND a.problem.status IN (
              com.localproblemsolver.entity.ProblemStatus.OPEN,
              com.localproblemsolver.entity.ProblemStatus.VALIDATED,
              com.localproblemsolver.entity.ProblemStatus.ASSIGNED,
              com.localproblemsolver.entity.ProblemStatus.IN_PROGRESS,
              com.localproblemsolver.entity.ProblemStatus.REOPENED
          )
    """)
    long countActiveProblemsByAuthority(
            @Param("authorityId") Long authorityId
    );

    @Query("""
        SELECT COUNT(a.id)
        FROM Assignment a
        WHERE a.authority.id = :authorityId
          AND a.problem.status =
              com.localproblemsolver.entity.ProblemStatus.RESOLVED
    """)
    long countResolvedProblemsByAuthority(
            @Param("authorityId") Long authorityId
    );

    @Query("""
        SELECT COUNT(a.id)
        FROM Assignment a
        WHERE a.authority.id = :authorityId
          AND a.problem.status =
              com.localproblemsolver.entity.ProblemStatus.CLOSED
    """)
    long countClosedProblemsByAuthority(
            @Param("authorityId") Long authorityId
    );
}
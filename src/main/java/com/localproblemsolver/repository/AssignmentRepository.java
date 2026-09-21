package com.localproblemsolver.repository;

import com.localproblemsolver.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository
        extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByProblemId(Long problemId);

    boolean existsByProblemIdAndAuthorityId(
            Long problemId,
            Long authorityId
    );

    @Query("""
        SELECT a.authority.id,
               a.authority.name,
               COUNT(a.id),
               SUM(
                   CASE
                       WHEN a.problem.status IN (
                           com.localproblemsolver.entity.ProblemStatus.OPEN,
                           com.localproblemsolver.entity.ProblemStatus.VALIDATED,
                           com.localproblemsolver.entity.ProblemStatus.ASSIGNED,
                           com.localproblemsolver.entity.ProblemStatus.IN_PROGRESS,
                           com.localproblemsolver.entity.ProblemStatus.REOPENED
                       )
                       THEN 1
                       ELSE 0
                   END
               ),
               SUM(
                   CASE
                       WHEN a.problem.status =
                           com.localproblemsolver.entity.ProblemStatus.RESOLVED
                       THEN 1
                       ELSE 0
                   END
               ),
               SUM(
                   CASE
                       WHEN a.problem.status =
                           com.localproblemsolver.entity.ProblemStatus.CLOSED
                       THEN 1
                       ELSE 0
                   END
               ),
               SUM(
                   CASE
                       WHEN s.breached = true
                       THEN 1
                       ELSE 0
                   END
               )
        FROM Assignment a
        LEFT JOIN Sla s
            ON s.problem.id = a.problem.id
        GROUP BY a.authority.id, a.authority.name
        ORDER BY COUNT(a.id) DESC
    """)
    List<Object[]> getAuthorityAnalytics();
}
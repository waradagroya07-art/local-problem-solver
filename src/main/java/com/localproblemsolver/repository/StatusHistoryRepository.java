package com.localproblemsolver.repository;

import com.localproblemsolver.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusHistoryRepository
        extends JpaRepository<StatusHistory, Long> {

    List<StatusHistory> findByProblemIdOrderByChangedAtAsc(
            Long problemId
    );

    @Query("""
        SELECT sh.problem.id,
               sh.oldStatus,
               sh.newStatus,
               sh.changedAt,
               sh.problem.createdAt
        FROM StatusHistory sh
        ORDER BY sh.problem.id, sh.changedAt
    """)
    List<Object[]> findAllStatusHistoryForLifecycle();

    @Query("""
        SELECT sh.oldStatus, sh.newStatus, COUNT(sh.id)
        FROM StatusHistory sh
        GROUP BY sh.oldStatus, sh.newStatus
        ORDER BY COUNT(sh.id) DESC
    """)
    List<Object[]> countStatusTransitions();
}
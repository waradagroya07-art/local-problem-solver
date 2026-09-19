package com.localproblemsolver.repository;

import com.localproblemsolver.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusHistoryRepository
        extends JpaRepository<StatusHistory, Long> {

    List<StatusHistory> findByProblemIdOrderByChangedAtAsc(Long problemId);
}
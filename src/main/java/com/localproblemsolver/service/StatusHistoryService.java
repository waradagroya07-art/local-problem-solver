package com.localproblemsolver.service;

import com.localproblemsolver.dto.StatusHistoryResponse;
import com.localproblemsolver.entity.StatusHistory;
import com.localproblemsolver.exception.ProblemNotFoundException;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusHistoryService {

    private final StatusHistoryRepository statusHistoryRepository;
    private final ProblemRepository problemRepository;


    public StatusHistoryService(
            StatusHistoryRepository statusHistoryRepository,
            ProblemRepository problemRepository) {

        this.statusHistoryRepository = statusHistoryRepository;
        this.problemRepository = problemRepository;
    }


    // =========================================================
    // GET STATUS HISTORY FOR A PROBLEM
    // =========================================================

    public List<StatusHistoryResponse> getStatusHistory(
            Long problemId) {

        // First make sure the problem exists
        problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: "
                                        + problemId
                        )
                );


        // Fetch history ordered from oldest to newest
        return statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(problemId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // CONVERT ENTITY → DTO
    // =========================================================

    private StatusHistoryResponse convertToResponse(
            StatusHistory statusHistory) {

        String changedByEmail = null;

        if (statusHistory.getChangedBy() != null) {
            changedByEmail =
                    statusHistory.getChangedBy().getEmail();
        }


        return new StatusHistoryResponse(
                statusHistory.getId(),
                statusHistory.getOldStatus(),
                statusHistory.getNewStatus(),
                changedByEmail,
                statusHistory.getChangedAt()
        );
    }
}
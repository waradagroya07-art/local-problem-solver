package com.localproblemsolver.service;

import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.repository.ProblemRepository;
import org.springframework.stereotype.Service;
import com.localproblemsolver.exception.InvalidStatusTransitionException;
import com.localproblemsolver.exception.ProblemNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public Problem saveProblem(Problem problem) {

        problem.setStatus(ProblemStatus.OPEN);
        problem.setPriority(Priority.MEDIUM);

        LocalDateTime now = LocalDateTime.now();
        problem.setCreatedAt(now);
        problem.setUpdatedAt(now);

        return problemRepository.save(problem);
    }

    public List<ProblemResponse> findAllProblems() {

        return problemRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ProblemResponse convertToResponse(Problem problem) {

        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getDescription(),
                problem.getSeverity(),
                problem.getStatus(),
                problem.getPriority(),
                problem.getLocation(),
                problem.getLatitude(),
                problem.getLongitude(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }
    public Problem changeStatus(Long id, ProblemStatus newStatus) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() ->
                        new ProblemNotFoundException("Problem not found with id: " + id)
                );

        ProblemStatus currentStatus = problem.getStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Invalid status transition: " + currentStatus + " → " + newStatus
            );
        }

        problem.setStatus(newStatus);
        problem.setUpdatedAt(LocalDateTime.now());

        return problemRepository.save(problem);
    }

    private boolean isValidStatusTransition(
            ProblemStatus currentStatus,
            ProblemStatus newStatus) {

        if (currentStatus == ProblemStatus.OPEN
                && (newStatus == ProblemStatus.VALIDATED
                || newStatus == ProblemStatus.REJECTED
                || newStatus == ProblemStatus.DUPLICATE)) {
            return true;
        }

        if (currentStatus == ProblemStatus.VALIDATED
                && (newStatus == ProblemStatus.ASSIGNED
                || newStatus == ProblemStatus.REJECTED
                || newStatus == ProblemStatus.DUPLICATE)) {
            return true;
        }

        if (currentStatus == ProblemStatus.ASSIGNED
                && newStatus == ProblemStatus.IN_PROGRESS) {
            return true;
        }

        if (currentStatus == ProblemStatus.IN_PROGRESS
                && newStatus == ProblemStatus.RESOLVED) {
            return true;
        }

        if (currentStatus == ProblemStatus.RESOLVED
                && newStatus == ProblemStatus.CLOSED) {
            return true;
        }

        if (currentStatus == ProblemStatus.CLOSED
                && newStatus == ProblemStatus.REOPENED) {
            return true;
        }

        if (currentStatus == ProblemStatus.REOPENED
                && newStatus == ProblemStatus.IN_PROGRESS) {
            return true;
        }

        return false;
    }
    public ProblemResponse findProblemById(Long id) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: " + id
                        )
                );

        return convertToResponse(problem);
    }
}
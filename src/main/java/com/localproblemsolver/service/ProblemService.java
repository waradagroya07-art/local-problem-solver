package com.localproblemsolver.service;

import com.localproblemsolver.dto.CategoryResponse;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Category;
import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.exception.InvalidStatusTransitionException;
import com.localproblemsolver.exception.ProblemNotFoundException;
import com.localproblemsolver.repository.CategoryRepository;
import com.localproblemsolver.repository.ProblemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;
    private final PriorityCalculationService priorityCalculationService;

    public ProblemService(
            ProblemRepository problemRepository,
            CategoryRepository categoryRepository,
            PriorityCalculationService priorityCalculationService) {

        this.problemRepository = problemRepository;
        this.categoryRepository = categoryRepository;
        this.priorityCalculationService = priorityCalculationService;
    }

    public Problem saveProblem(Problem problem, Long categoryId) {

        if (categoryId != null) {

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Category not found with id: " + categoryId
                            )
                    );

            problem.setCategory(category);
        }

        problem.setStatus(ProblemStatus.OPEN);

        LocalDateTime now = LocalDateTime.now();

        problem.setCreatedAt(now);
        problem.setUpdatedAt(now);

        /*
         * Calculate priority after setting the creation time.
         */
        Priority priority =
                priorityCalculationService.calculatePriority(problem);

        problem.setPriority(priority);

        return problemRepository.save(problem);
    }

    public List<ProblemResponse> findAllProblems() {

        return problemRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
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

    public Problem changeStatus(
            Long id,
            ProblemStatus newStatus) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: " + id
                        )
                );

        ProblemStatus currentStatus = problem.getStatus();

        if (!isValidStatusTransition(
                currentStatus,
                newStatus)) {

            throw new InvalidStatusTransitionException(
                    "Invalid status transition: "
                            + currentStatus
                            + " → "
                            + newStatus
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
                || newStatus == ProblemStatus.ASSIGNED
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

    public ProblemResponse convertToResponse(Problem problem) {

        CategoryResponse categoryResponse = null;

        if (problem.getCategory() != null) {

            categoryResponse = new CategoryResponse(
                    problem.getCategory().getId(),
                    problem.getCategory().getName()
            );
        }

        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getDescription(),
                problem.getSeverity(),
                problem.getStatus(),
                problem.getPriority(),
                categoryResponse,
                problem.getLocation(),
                problem.getLatitude(),
                problem.getLongitude(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }
}
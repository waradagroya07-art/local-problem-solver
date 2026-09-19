package com.localproblemsolver.service;

import com.localproblemsolver.dto.CategoryResponse;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Category;
import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.entity.StatusHistory;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.exception.InvalidStatusTransitionException;
import com.localproblemsolver.exception.ProblemNotFoundException;
import com.localproblemsolver.repository.CategoryRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.StatusHistoryRepository;
import com.localproblemsolver.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final PriorityCalculationService priorityCalculationService;

    public ProblemService(
            ProblemRepository problemRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            StatusHistoryRepository statusHistoryRepository,
            PriorityCalculationService priorityCalculationService) {

        this.problemRepository = problemRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.priorityCalculationService = priorityCalculationService;
    }


    // =========================================================
    // CREATE PROBLEM
    // =========================================================

    public Problem saveProblem(
            Problem problem,
            Long categoryId,
            String userEmail) {

        // Find category if categoryId is provided
        if (categoryId != null) {

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Category not found with id: " + categoryId
                            )
                    );

            problem.setCategory(category);
        }

        // Find the logged-in user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with email: " + userEmail
                        )
                );

        // Attach problem to the logged-in user
        problem.setUser(user);

        // Every newly created problem starts with OPEN
        problem.setStatus(ProblemStatus.OPEN);

        LocalDateTime now = LocalDateTime.now();

        problem.setCreatedAt(now);
        problem.setUpdatedAt(now);

        // Calculate initial priority
        Priority priority =
                priorityCalculationService.calculatePriority(problem);

        problem.setPriority(priority);

        return problemRepository.save(problem);
    }


    // =========================================================
    // GET ALL PROBLEMS
    // =========================================================

    public List<ProblemResponse> findAllProblems() {

        return problemRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // GET PROBLEM BY ID
    // =========================================================

    public ProblemResponse findProblemById(Long id) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: " + id
                        )
                );

        return convertToResponse(problem);
    }


    // =========================================================
    // CHANGE STATUS
    // =========================================================

    public Problem changeStatus(
            Long id,
            ProblemStatus newStatus,
            String userEmail) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: " + id
                        )
                );

        // Remember the old status before changing it
        ProblemStatus oldStatus = problem.getStatus();

        // Check whether the transition is allowed
        if (!isValidStatusTransition(
                oldStatus,
                newStatus)) {

            throw new InvalidStatusTransitionException(
                    "Invalid status transition: "
                            + oldStatus
                            + " → "
                            + newStatus
            );
        }

        // Find the user who performed the status change
        User changedBy = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Authenticated user not found"
                        )
                );

        // Update problem status
        problem.setStatus(newStatus);
        problem.setUpdatedAt(LocalDateTime.now());

        // Save updated problem
        Problem savedProblem =
                problemRepository.save(problem);

        // Create status history record
        StatusHistory statusHistory = new StatusHistory();

        statusHistory.setProblem(savedProblem);
        statusHistory.setOldStatus(oldStatus);
        statusHistory.setNewStatus(newStatus);
        statusHistory.setChangedBy(changedBy);
        statusHistory.setChangedAt(LocalDateTime.now());

        statusHistoryRepository.save(statusHistory);

        return savedProblem;
    }


    // =========================================================
    // CITIZEN CONFIRM PROBLEM
    // RESOLVED → CLOSED
    // =========================================================

    public Problem confirmProblem(
            Long problemId,
            String userEmail) {

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: "
                                        + problemId
                        )
                );

        // Check ownership
        verifyOwnership(problem, userEmail);

        // Problem must be RESOLVED
        if (problem.getStatus() != ProblemStatus.RESOLVED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only resolved problems can be confirmed"
            );
        }

        // RESOLVED → CLOSED
        return changeStatus(
                problemId,
                ProblemStatus.CLOSED,
                userEmail
        );
    }


    // =========================================================
    // CITIZEN REOPEN PROBLEM
    // RESOLVED → REOPENED
    // =========================================================

    public Problem reopenProblem(
            Long problemId,
            String userEmail) {

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: "
                                        + problemId
                        )
                );

        // Check ownership
        verifyOwnership(problem, userEmail);

        // Problem must be RESOLVED
        if (problem.getStatus() != ProblemStatus.RESOLVED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only resolved problems can be reopened"
            );
        }

        // RESOLVED → REOPENED
        return changeStatus(
                problemId,
                ProblemStatus.REOPENED,
                userEmail
        );
    }


    // =========================================================
    // VERIFY PROBLEM OWNERSHIP
    // =========================================================

    private void verifyOwnership(
            Problem problem,
            String userEmail) {

        if (problem.getUser() == null
                || problem.getUser().getEmail() == null
                || !problem.getUser()
                .getEmail()
                .equalsIgnoreCase(userEmail)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to modify this problem"
            );
        }
    }


    // =========================================================
    // VALID STATUS TRANSITIONS
    // =========================================================

    private boolean isValidStatusTransition(
            ProblemStatus currentStatus,
            ProblemStatus newStatus) {

        // OPEN
        // ↓
        // VALIDATED / ASSIGNED / REJECTED / DUPLICATE

        if (currentStatus == ProblemStatus.OPEN
                && (newStatus == ProblemStatus.VALIDATED
                || newStatus == ProblemStatus.ASSIGNED
                || newStatus == ProblemStatus.REJECTED
                || newStatus == ProblemStatus.DUPLICATE)) {

            return true;
        }


        // VALIDATED
        // ↓
        // ASSIGNED / REJECTED / DUPLICATE

        if (currentStatus == ProblemStatus.VALIDATED
                && (newStatus == ProblemStatus.ASSIGNED
                || newStatus == ProblemStatus.REJECTED
                || newStatus == ProblemStatus.DUPLICATE)) {

            return true;
        }


        // ASSIGNED
        // ↓
        // IN_PROGRESS

        if (currentStatus == ProblemStatus.ASSIGNED
                && newStatus == ProblemStatus.IN_PROGRESS) {

            return true;
        }


        // IN_PROGRESS
        // ↓
        // RESOLVED

        if (currentStatus == ProblemStatus.IN_PROGRESS
                && newStatus == ProblemStatus.RESOLVED) {

            return true;
        }


        // RESOLVED
        // ↓
        // CLOSED

        if (currentStatus == ProblemStatus.RESOLVED
                && newStatus == ProblemStatus.CLOSED) {

            return true;
        }


        // RESOLVED
        // ↓
        // REOPENED

        if (currentStatus == ProblemStatus.RESOLVED
                && newStatus == ProblemStatus.REOPENED) {

            return true;
        }


        // CLOSED
        // ↓
        // REOPENED

        if (currentStatus == ProblemStatus.CLOSED
                && newStatus == ProblemStatus.REOPENED) {

            return true;
        }


        // REOPENED
        // ↓
        // IN_PROGRESS

        if (currentStatus == ProblemStatus.REOPENED
                && newStatus == ProblemStatus.IN_PROGRESS) {

            return true;
        }

        return false;
    }


    // =========================================================
    // CONVERT ENTITY → RESPONSE DTO
    // =========================================================

    public ProblemResponse convertToResponse(
            Problem problem) {

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
                problem.getUpdatedAt(),
                problem.getDuplicateOf() != null
                        ? problem.getDuplicateOf().getId()
                        : null
        );
    }
    @Transactional
    public Problem markAsDuplicate(
            Long duplicateProblemId,
            Long originalProblemId,
            String userEmail) {

        // Find the duplicate problem
        Problem duplicateProblem = problemRepository.findById(duplicateProblemId)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: " + duplicateProblemId));

        // Find the original problem
        Problem originalProblem = problemRepository.findById(originalProblemId)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Original problem not found with id: " + originalProblemId));

        // A problem cannot be a duplicate of itself
        if (duplicateProblemId.equals(originalProblemId)) {
            throw new IllegalArgumentException(
                    "A problem cannot be a duplicate of itself");
        }

        // Original problem should not already be a duplicate
        if (originalProblem.getStatus() == ProblemStatus.DUPLICATE) {
            throw new IllegalArgumentException(
                    "The original problem is already marked as duplicate");
        }

        // Duplicate can only come from OPEN or VALIDATED
        if (duplicateProblem.getStatus() != ProblemStatus.OPEN
                && duplicateProblem.getStatus() != ProblemStatus.VALIDATED) {

            throw new IllegalArgumentException(
                    "Only OPEN or VALIDATED problems can be marked as duplicate");
        }

        // Link duplicate problem to the original problem
        duplicateProblem.setDuplicateOf(originalProblem);

        // Save the relationship first
        problemRepository.save(duplicateProblem);

        // Change status and create status history
        return changeStatus(
                duplicateProblemId,
                ProblemStatus.DUPLICATE,
                userEmail);
    }
    public Problem updateCategory(
            Long problemId,
            Long categoryId) {

        // Find the problem
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: " + problemId));

        // Find the new category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found with id: " + categoryId));

        // Update category
        problem.setCategory(category);

        // Update modification time
        problem.setUpdatedAt(LocalDateTime.now());

        // Save updated problem
        return problemRepository.save(problem);
    }
}
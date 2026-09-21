package com.localproblemsolver.controller;

import com.localproblemsolver.dto.ProblemRequest;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    // =========================================================
    // CREATE PROBLEM
    // =========================================================

    @PostMapping("/api/problems")
    @PreAuthorize("hasRole('CITIZEN')")
    public ProblemResponse createProblem(
            @Valid @RequestBody ProblemRequest request,
            Authentication authentication) {

        Problem problem = new Problem();

        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setSeverity(request.getSeverity());
        problem.setLocation(request.getLocation());
        problem.setLatitude(request.getLatitude());
        problem.setLongitude(request.getLongitude());

        String userEmail = authentication.getName();

        Problem savedProblem =
                problemService.saveProblem(
                        problem,
                        request.getCategoryId(),
                        userEmail
                );

        return problemService.convertToResponse(savedProblem);
    }

    // =========================================================
    // GET ALL PROBLEMS
    // =========================================================

    @GetMapping("/api/problems")
    @PreAuthorize("isAuthenticated()")
    public List<ProblemResponse> getAllProblems() {

        return problemService.findAllProblems();
    }

    // =========================================================
    // GET PROBLEM BY ID
    // =========================================================

    @GetMapping("/api/problems/{id}")
    @PreAuthorize("isAuthenticated()")
    public ProblemResponse getProblemById(
            @PathVariable Long id) {

        return problemService.findProblemById(id);
    }

    // =========================================================
    // CHANGE PROBLEM STATUS
    // =========================================================

    @PatchMapping("/api/problems/{id}/status")
    @PreAuthorize("hasAnyRole('MODERATOR', 'AUTHORITY', 'SUPER_ADMIN')")
    public ProblemResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody com.localproblemsolver.dto.StatusUpdateRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem = problemService.changeStatus(
                id,
                request.getStatus(),
                userEmail
        );

        return problemService.convertToResponse(problem);
    }

    // =========================================================
    // CITIZEN CONFIRM PROBLEM
    // RESOLVED → CLOSED
    // =========================================================

    @PostMapping("/api/problems/{id}/confirm")
    @PreAuthorize("hasRole('CITIZEN')")
    public ProblemResponse confirmProblem(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem = problemService.confirmProblem(
                id,
                userEmail
        );

        return problemService.convertToResponse(problem);
    }

    // =========================================================
    // CITIZEN REOPEN PROBLEM
    // RESOLVED → REOPENED
    // =========================================================

    @PostMapping("/api/problems/{id}/reopen")
    @PreAuthorize("hasRole('CITIZEN')")
    public ProblemResponse reopenProblem(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem = problemService.reopenProblem(
                id,
                userEmail
        );

        return problemService.convertToResponse(problem);
    }
}
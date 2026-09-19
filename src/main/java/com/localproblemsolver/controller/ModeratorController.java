package com.localproblemsolver.controller;

import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.service.ProblemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.localproblemsolver.dto.MarkDuplicateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
@RestController
@RequestMapping("/api/moderator")
@PreAuthorize("hasRole('MODERATOR')")
public class ModeratorController {

    private final ProblemService problemService;

    public ModeratorController(ProblemService problemService) {
        this.problemService = problemService;
    }


    // =========================================================
    // VALIDATE PROBLEM
    // OPEN → VALIDATED
    // =========================================================

    @PatchMapping("/problems/{id}/validate")
    public ProblemResponse validateProblem(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem = problemService.changeStatus(
                id,
                ProblemStatus.VALIDATED,
                userEmail
        );

        return problemService.convertToResponse(problem);
    }


    // =========================================================
    // REJECT PROBLEM
    // OPEN → REJECTED
    // =========================================================

    @PatchMapping("/problems/{id}/reject")
    public ProblemResponse rejectProblem(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem = problemService.changeStatus(
                id,
                ProblemStatus.REJECTED,
                userEmail
        );

        return problemService.convertToResponse(problem);
    }


    // =========================================================
    // MARK PROBLEM AS DUPLICATE
    // OPEN / VALIDATED → DUPLICATE
    // =========================================================

    @PatchMapping("/problems/{id}/duplicate")
    public ProblemResponse markAsDuplicate(
            @PathVariable Long id,
            @Valid @RequestBody MarkDuplicateRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem = problemService.markAsDuplicate(
                id,
                request.getOriginalProblemId(),
                userEmail);

        return problemService.convertToResponse(problem);
    }
}
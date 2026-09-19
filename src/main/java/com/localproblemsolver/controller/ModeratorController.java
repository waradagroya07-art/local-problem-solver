package com.localproblemsolver.controller;

import com.localproblemsolver.dto.CategoryUpdateRequest;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderator")
@PreAuthorize("hasRole('MODERATOR')")
public class ModeratorController {

    private final ProblemService problemService;

    public ModeratorController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PatchMapping("/problems/{id}/validate")
    public ProblemResponse validateProblem(
            @PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem = problemService.changeStatus(
                id,
                ProblemStatus.VALIDATED,
                userEmail);

        return problemService.convertToResponse(problem);
    }

    @PatchMapping("/problems/{id}/reject")
    public ProblemResponse rejectProblem(
            @PathVariable Long id,
            org.springframework.security.core.Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem = problemService.changeStatus(
                id,
                ProblemStatus.REJECTED,
                userEmail);

        return problemService.convertToResponse(problem);
    }

    @PatchMapping("/problems/{id}/duplicate")
    public ProblemResponse markAsDuplicate(
            @PathVariable Long id,
            @Valid @RequestBody com.localproblemsolver.dto.MarkDuplicateRequest request,
            org.springframework.security.core.Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem = problemService.markAsDuplicate(
                id,
                request.getOriginalProblemId(),
                userEmail);

        return problemService.convertToResponse(problem);
    }

    @PatchMapping("/problems/{id}/category")
    public ProblemResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {

        Problem problem = problemService.updateCategory(
                id,
                request.getCategoryId());

        return problemService.convertToResponse(problem);
    }
}
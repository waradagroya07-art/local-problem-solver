package com.localproblemsolver.controller;

import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.service.ProblemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citizen")
@PreAuthorize("hasRole('CITIZEN')")
public class CitizenController {

    private final ProblemService problemService;

    public CitizenController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PatchMapping("/problems/{id}/confirm")
    public ProblemResponse confirmProblem(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem =
                problemService.confirmProblem(id, userEmail);

        return problemService.convertToResponse(problem);
    }


    @PatchMapping("/problems/{id}/reopen")
    public ProblemResponse reopenProblem(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Problem problem =
                problemService.reopenProblem(id, userEmail);

        return problemService.convertToResponse(problem);
    }
}
package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AssignmentResponse;
import com.localproblemsolver.service.AssignmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
@PreAuthorize("hasRole('MODERATOR')")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(
            AssignmentService assignmentService) {

        this.assignmentService = assignmentService;
    }

    @PostMapping("/problems/{problemId}")
    public AssignmentResponse assignProblem(
            @PathVariable Long problemId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return assignmentService.assignProblem(
                problemId,
                userEmail
        );
    }

    @GetMapping("/problems/{problemId}")
    public AssignmentResponse getAssignment(
            @PathVariable Long problemId) {

        return assignmentService.getAssignment(problemId);
    }
}
package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AssignmentResponse;
import com.localproblemsolver.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(
            AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/problems/{problemId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> assignProblem(
            @PathVariable Long problemId,
            Authentication authentication) {

        String userEmail =
                authentication.getName();

        return ResponseEntity.ok(
                assignmentService.assignProblem(
                        problemId,
                        userEmail
                )
        );
    }

    @GetMapping("/problems/{problemId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> getAssignment(
            @PathVariable Long problemId) {

        return ResponseEntity.ok(
                assignmentService.getAssignment(problemId)
        );
    }
}
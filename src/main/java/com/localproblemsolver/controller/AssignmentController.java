package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AssignmentResponse;
import com.localproblemsolver.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(
            AssignmentService assignmentService) {

        this.assignmentService = assignmentService;
    }

    // =========================================================
    // ASSIGN PROBLEM
    // =========================================================

    @PostMapping("/problems/{problemId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> assignProblem(
            @PathVariable Long problemId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                assignmentService.assignProblem(
                        problemId,
                        userEmail
                )
        );
    }

    // =========================================================
    // ACCEPT ASSIGNMENT
    // =========================================================

    @PutMapping("/problems/{problemId}/accept")
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<AssignmentResponse> acceptAssignment(
            @PathVariable Long problemId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                assignmentService.acceptAssignment(
                        problemId,
                        userEmail
                )
        );
    }
    @PutMapping("/problems/{problemId}/decline")
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<AssignmentResponse> declineAssignment(
            @PathVariable Long problemId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                assignmentService.declineAssignment(
                        problemId,
                        userEmail
                )
        );
    }
    @PutMapping("/problems/{problemId}/reassign")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> reassignProblem(
            @PathVariable Long problemId,
            @RequestBody Map<String, Long> request) {

        Long newAuthorityId = request.get("authorityId");

        return ResponseEntity.ok(
                assignmentService.reassignProblem(
                        problemId,
                        newAuthorityId
                )
        );
    }
    // =========================================================
    // GET ASSIGNMENT
    // =========================================================

    @GetMapping("/problems/{problemId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<AssignmentResponse> getAssignment(
            @PathVariable Long problemId) {

        return ResponseEntity.ok(
                assignmentService.getAssignment(problemId)
        );
    }
}
package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AssignmentResponse;
import com.localproblemsolver.service.AssignmentService;
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
    public AssignmentResponse assignProblem(
            @PathVariable Long problemId) {

        return assignmentService.assignProblem(problemId);
    }

    @GetMapping("/problems/{problemId}")
    public AssignmentResponse getAssignment(
            @PathVariable Long problemId) {

        return assignmentService.getAssignment(problemId);
    }
}
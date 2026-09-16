package com.localproblemsolver.controller;

import com.localproblemsolver.dto.ProblemRequest;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.dto.StatusUpdateRequest;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/api/problems/test")
    public String test() {
        return "Problem Controller is working!";
    }

    @PostMapping("/api/problems")
    public ProblemResponse createProblem(
            @Valid @RequestBody ProblemRequest request) {

        Problem problem = new Problem();

        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setSeverity(request.getSeverity());
        problem.setLocation(request.getLocation());
        problem.setLatitude(request.getLatitude());
        problem.setLongitude(request.getLongitude());

        Problem savedProblem = problemService.saveProblem(
                problem,
                request.getCategoryId()
        );

        return problemService.convertToResponse(savedProblem);
    }

    @GetMapping("/api/problems")
    public List<ProblemResponse> getAllProblems() {
        return problemService.findAllProblems();
    }

    @GetMapping("/api/problems/{id}")
    public ProblemResponse getProblemById(@PathVariable Long id) {
        return problemService.findProblemById(id);
    }

    @PatchMapping("/api/problems/{id}/status")
    public ProblemResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {

        Problem problem = problemService.changeStatus(
                id,
                request.getStatus()
        );

        return problemService.convertToResponse(problem);
    }
}
package com.localproblemsolver.controller;

import com.localproblemsolver.dto.ProblemRequest;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.localproblemsolver.dto.StatusUpdateRequest;
import java.util.List;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

        Problem savedProblem = problemService.saveProblem(problem);

        return new ProblemResponse(
                savedProblem.getId(),
                savedProblem.getTitle(),
                savedProblem.getDescription(),
                savedProblem.getSeverity(),
                savedProblem.getStatus(),
                savedProblem.getPriority(),
                savedProblem.getLocation(),
                savedProblem.getLatitude(),
                savedProblem.getLongitude(),
                savedProblem.getCreatedAt(),
                savedProblem.getUpdatedAt()
        );
    }

    @GetMapping("/api/problems")
    public List<ProblemResponse> getAllProblems() {
        return problemService.findAllProblems();
    }
    @PatchMapping("/api/problems/{id}/status")
    public ProblemResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {

        Problem problem = problemService.changeStatus(
                id,
                request.getStatus()
        );

        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getDescription(),
                problem.getSeverity(),
                problem.getStatus(),
                problem.getPriority(),
                problem.getLocation(),
                problem.getLatitude(),
                problem.getLongitude(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }
    @GetMapping("/api/problems/{id}")
    public ProblemResponse getProblemById(@PathVariable Long id) {

        return problemService.findProblemById(id);
    }
}
package com.localproblemsolver.service;

import com.localproblemsolver.dto.AssignmentResponse;
import com.localproblemsolver.dto.AuthorityResponse;
import com.localproblemsolver.dto.CategoryResponse;
import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.AuthorityRepository;
import com.localproblemsolver.repository.ProblemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ProblemRepository problemRepository;
    private final AuthorityRepository authorityRepository;
    private final ProblemService problemService;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            ProblemRepository problemRepository,
            AuthorityRepository authorityRepository,
            ProblemService problemService) {

        this.assignmentRepository = assignmentRepository;
        this.problemRepository = problemRepository;
        this.authorityRepository = authorityRepository;
        this.problemService = problemService;
    }

    @Transactional
    public AssignmentResponse assignProblem(
            Long problemId,
            String userEmail) {

        // 1. Find the problem
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Problem not found with id: " + problemId
                        )
                );

        // 2. Problem must have a category
        if (problem.getCategory() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Problem must have a category before assignment"
            );
        }

        // 3. Problem must be validated
        if (problem.getStatus() != ProblemStatus.VALIDATED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only validated problems can be assigned"
            );
        }

        // 4. Check whether problem is already assigned
        if (assignmentRepository
                .findByProblemId(problemId)
                .isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Problem is already assigned"
            );
        }

        // 5. Find authorities responsible for this category
        List<Authority> authorities =
                authorityRepository.findByCategoryId(
                        problem.getCategory().getId()
                );

        if (authorities.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No authority found for this category"
            );
        }

        // 6. Find authority based on geographic zone
        Authority selectedAuthority = authorities.stream()
                .filter(authority ->
                        problem.getLocation() != null
                                && authority.getZone() != null
                                && problem.getLocation()
                                .toLowerCase()
                                .contains(
                                        authority.getZone()
                                                .toLowerCase()
                                )
                )
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No authority found for the problem location"
                        )
                );

        // 7. Create assignment
        Assignment assignment = new Assignment();

        assignment.setProblem(problem);
        assignment.setAuthority(selectedAuthority);
        assignment.setAssignedAt(LocalDateTime.now());

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        // 8. Change status through ProblemService
        // This also creates the StatusHistory record.
        problemService.changeStatus(
                problemId,
                ProblemStatus.ASSIGNED,
                userEmail
        );

        return convertToResponse(savedAssignment);
    }


    // =========================================================
    // GET ASSIGNMENT
    // =========================================================

    public AssignmentResponse getAssignment(Long problemId) {

        Assignment assignment =
                assignmentRepository.findByProblemId(problemId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Assignment not found for problem id: "
                                                + problemId
                                )
                        );

        return convertToResponse(assignment);
    }


    // =========================================================
    // CONVERT ENTITY → RESPONSE DTO
    // =========================================================

    private AssignmentResponse convertToResponse(
            Assignment assignment) {

        Authority authority = assignment.getAuthority();

        CategoryResponse categoryResponse =
                new CategoryResponse(
                        authority.getCategory().getId(),
                        authority.getCategory().getName()
                );

        AuthorityResponse authorityResponse =
                new AuthorityResponse(
                        authority.getId(),
                        authority.getName(),
                        categoryResponse,
                        authority.getZone()
                );

        return new AssignmentResponse(
                assignment.getId(),
                assignment.getProblem().getId(),
                authorityResponse,
                assignment.getAssignedAt()
        );
    }
}
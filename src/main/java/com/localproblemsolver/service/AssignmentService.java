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

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            ProblemRepository problemRepository,
            AuthorityRepository authorityRepository) {

        this.assignmentRepository = assignmentRepository;
        this.problemRepository = problemRepository;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public AssignmentResponse assignProblem(Long problemId) {

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Problem not found with id: "
                                        + problemId
                        )
                );

        if (problem.getCategory() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Problem must have a category before assignment"
            );
        }

        if (problem.getStatus() != ProblemStatus.VALIDATED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only VALIDATED problems can be assigned"
            );
        }

        if (assignmentRepository
                .findByProblemId(problemId)
                .isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Problem is already assigned"
            );
        }

        List<Authority> authorities =
                authorityRepository.findByCategoryId(
                        problem.getCategory().getId()
                );

        Authority matchingAuthority = authorities
                .stream()
                .filter(authority ->
                        locationMatchesZone(
                                problem.getLocation(),
                                authority.getZone()
                        )
                )
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No responsible authority found "
                                        + "for this problem's category "
                                        + "and location"
                        )
                );

        Assignment assignment = new Assignment();

        assignment.setProblem(problem);
        assignment.setAuthority(matchingAuthority);
        assignment.setAssignedAt(LocalDateTime.now());

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        problem.setStatus(ProblemStatus.ASSIGNED);
        problem.setUpdatedAt(LocalDateTime.now());

        problemRepository.save(problem);

        return convertToResponse(savedAssignment);
    }

    public AssignmentResponse getAssignment(Long problemId) {

        Assignment assignment =
                assignmentRepository.findByProblemId(problemId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No assignment found for "
                                                + "problem id: "
                                                + problemId
                                )
                        );

        return convertToResponse(assignment);
    }

    private boolean locationMatchesZone(
            String location,
            String zone) {

        if (location == null || zone == null) {
            return false;
        }

        return location
                .toLowerCase()
                .contains(zone.toLowerCase());
    }

    private AssignmentResponse convertToResponse(
            Assignment assignment) {

        Authority authority =
                assignment.getAuthority();

        CategoryResponse categoryResponse = null;

        if (authority.getCategory() != null) {

            categoryResponse = new CategoryResponse(
                    authority.getCategory().getId(),
                    authority.getCategory().getName()
            );
        }

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
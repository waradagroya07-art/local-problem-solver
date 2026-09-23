package com.localproblemsolver.service;

import com.localproblemsolver.dto.AssignmentResponse;
import com.localproblemsolver.dto.AuthorityResponse;
import com.localproblemsolver.dto.CategoryResponse;
import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.AssignmentStatus;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.AuthorityRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ProblemService problemService;
    private final NotificationService notificationService;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            ProblemRepository problemRepository,
            AuthorityRepository authorityRepository,
            UserRepository userRepository,
            ProblemService problemService,
            NotificationService notificationService) {

        this.assignmentRepository = assignmentRepository;
        this.problemRepository = problemRepository;
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.problemService = problemService;
        this.notificationService = notificationService;
    }

    // =========================================================
    // ASSIGN PROBLEM
    // =========================================================

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
        assignment.setStatus(AssignmentStatus.PENDING);

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        // 8. Change problem status through ProblemService
        problemService.changeStatus(
                problemId,
                ProblemStatus.ASSIGNED,
                userEmail
        );

        // 9. Find the user linked to the selected authority
        User authorityUser = userRepository
                .findByAuthorityId(selectedAuthority.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No user found for authority id: "
                                        + selectedAuthority.getId()
                        )
                );

        // 10. Notify authority
        notificationService.createNotification(
                "Problem #" + problemId
                        + " has been assigned to your authority.",
                NotificationType.PROBLEM_ASSIGNED,
                authorityUser.getEmail()
        );

        return convertToResponse(savedAssignment);
    }

    // =========================================================
    // ACCEPT ASSIGNMENT
    // =========================================================

    @Transactional
    public AssignmentResponse acceptAssignment(
            Long problemId,
            String userEmail) {

        // 1. Find assignment
        Assignment assignment =
                assignmentRepository.findByProblemId(problemId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Assignment not found for problem id: "
                                                + problemId
                                )
                        );

        // 2. Find logged-in user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        // 3. User must be an AUTHORITY
        if (user.getRole() == null
                || !user.getRole().name().equals("AUTHORITY")) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the assigned authority can accept an assignment"
            );
        }

        // 4. User must have an authority linked
        if (user.getAuthority() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User is not linked to an authority"
            );
        }

        // 5. Verify this is the authority assigned to the problem
        if (!user.getAuthority().getId()
                .equals(assignment.getAuthority().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to accept this assignment"
            );
        }

        // 6. Assignment must currently be PENDING
        if (assignment.getStatus() != AssignmentStatus.PENDING) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending assignments can be accepted"
            );
        }

        // 7. Problem must currently be ASSIGNED
        if (assignment.getProblem().getStatus()
                != ProblemStatus.ASSIGNED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Problem must be in ASSIGNED status"
            );
        }

        // 8. Change assignment status
        assignment.setStatus(AssignmentStatus.ACCEPTED);

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        // 9. Change problem status to IN_PROGRESS
        problemService.changeStatus(
                problemId,
                ProblemStatus.IN_PROGRESS,
                userEmail
        );

        return convertToResponse(savedAssignment);
    }
    @Transactional
    public AssignmentResponse declineAssignment(
            Long problemId,
            String userEmail) {

        Assignment assignment =
                assignmentRepository.findByProblemId(problemId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Assignment not found for problem id: "
                                                + problemId
                                )
                        );

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        if (user.getRole() == null
                || !user.getRole().name().equals("AUTHORITY")) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the assigned authority can decline an assignment"
            );
        }

        if (user.getAuthority() == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User is not linked to an authority"
            );
        }

        if (!user.getAuthority().getId()
                .equals(assignment.getAuthority().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to decline this assignment"
            );
        }

        if (assignment.getStatus() != AssignmentStatus.PENDING) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending assignments can be declined"
            );
        }

        if (assignment.getProblem().getStatus()
                != ProblemStatus.ASSIGNED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Problem must be in ASSIGNED status"
            );
        }

        assignment.setStatus(AssignmentStatus.DECLINED);

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        return convertToResponse(savedAssignment);
    }
    @Transactional
    public AssignmentResponse reassignProblem(
            Long problemId,
            Long newAuthorityId) {

        Assignment assignment =
                assignmentRepository.findByProblemId(problemId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Assignment not found for problem id: "
                                                + problemId
                                )
                        );

        if (assignment.getStatus() != AssignmentStatus.DECLINED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only declined assignments can be reassigned"
            );
        }

        Problem problem = assignment.getProblem();

        Authority newAuthority =
                authorityRepository.findById(newAuthorityId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Authority not found with id: "
                                                + newAuthorityId
                                )
                        );

        // Category must match
        if (problem.getCategory() == null
                || !problem.getCategory().getId()
                .equals(newAuthority.getCategory().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Authority category does not match problem category"
            );
        }

        // Location/zone must match
        if (problem.getLocation() == null
                || newAuthority.getZone() == null
                || !problem.getLocation()
                .toLowerCase()
                .contains(
                        newAuthority.getZone().toLowerCase()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Authority zone does not match problem location"
            );
        }

        // Prevent assigning back to the same authority
        if (assignment.getAuthority().getId()
                .equals(newAuthority.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Problem cannot be reassigned to the same authority"
            );
        }

        // Make sure the new authority has a linked user
        User authorityUser = userRepository
                .findByAuthorityId(newAuthority.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No user found for authority id: "
                                        + newAuthority.getId()
                        )
                );

        // Reassign
        assignment.setAuthority(newAuthority);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setStatus(AssignmentStatus.PENDING);

        Assignment savedAssignment =
                assignmentRepository.save(assignment);

        // Notify the new authority
        notificationService.createNotification(
                "Problem #" + problemId
                        + " has been reassigned to your authority.",
                NotificationType.PROBLEM_ASSIGNED,
                authorityUser.getEmail()
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
                assignment.getAssignedAt(),
                assignment.getStatus()
        );
    }
}
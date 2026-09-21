package com.localproblemsolver.service;

import com.localproblemsolver.dto.StatusHistoryResponse;
import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.StatusHistory;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.exception.ProblemNotFoundException;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.StatusHistoryRepository;
import com.localproblemsolver.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StatusHistoryService {

    private final StatusHistoryRepository statusHistoryRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;

    public StatusHistoryService(
            StatusHistoryRepository statusHistoryRepository,
            ProblemRepository problemRepository,
            UserRepository userRepository,
            AssignmentRepository assignmentRepository) {

        this.statusHistoryRepository = statusHistoryRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    // =========================================================
    // GET STATUS HISTORY FOR A PROBLEM
    // =========================================================

    public List<StatusHistoryResponse> getStatusHistory(
            Long problemId,
            String userEmail) {

        // 1. Find the problem
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ProblemNotFoundException(
                                "Problem not found with id: " + problemId
                        )
                );

        // 2. Find the logged-in user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found."
                        )
                );

        // 3. Check authorization
        validateAccess(problem, user);

        // 4. Fetch history ordered from oldest to newest
        return statusHistoryRepository
                .findByProblemIdOrderByChangedAtAsc(problemId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // AUTHORIZATION
    // =========================================================

    private void validateAccess(
            Problem problem,
            User user) {

        Role role = user.getRole();

        // SUPER_ADMIN can view every problem's history.
        if (role == Role.SUPER_ADMIN) {
            return;
        }

        // MODERATOR can view every problem's history.
        if (role == Role.MODERATOR) {
            return;
        }

        // CITIZEN can view only their own problem's history.
        if (role == Role.CITIZEN) {

            if (!problem.getUser().getId().equals(user.getId())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not allowed to view this problem's status history."
                );
            }

            return;
        }

        // AUTHORITY can view only problems assigned
        // to their authority.
        if (role == Role.AUTHORITY) {

            if (user.getAuthority() == null) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Authority is not assigned to this user."
                );
            }

            Assignment assignment =
                    assignmentRepository.findByProblemId(problem.getId())
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.FORBIDDEN,
                                            "This problem is not assigned to you."
                                    )
                            );

            if (!assignment.getAuthority().getId()
                    .equals(user.getAuthority().getId())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not allowed to view this problem's status history."
                );
            }

            return;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not allowed to view status history."
        );
    }

    // =========================================================
    // CONVERT ENTITY → DTO
    // =========================================================

    private StatusHistoryResponse convertToResponse(
            StatusHistory statusHistory) {

        String changedByEmail = null;

        if (statusHistory.getChangedBy() != null) {
            changedByEmail =
                    statusHistory.getChangedBy().getEmail();
        }

        return new StatusHistoryResponse(
                statusHistory.getId(),
                statusHistory.getOldStatus(),
                statusHistory.getNewStatus(),
                changedByEmail,
                statusHistory.getChangedAt()
        );
    }
}
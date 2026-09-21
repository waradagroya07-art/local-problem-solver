package com.localproblemsolver.service;

import com.localproblemsolver.dto.CommentRequest;
import com.localproblemsolver.dto.CommentResponse;
import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.Comment;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.CommentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProblemRepository problemRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;

    public CommentService(
            CommentRepository commentRepository,
            ProblemRepository problemRepository,
            NotificationService notificationService,
            UserRepository userRepository,
            AssignmentRepository assignmentRepository) {

        this.commentRepository = commentRepository;
        this.problemRepository = problemRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public CommentResponse addComment(
            Long problemId,
            CommentRequest request,
            String userEmail) {

        Problem problem = getProblem(problemId);
        User user = getUser(userEmail);

        // Check whether the logged-in user is allowed
        // to comment on this problem.
        validateCommentAccess(problem, user);

        Comment comment = new Comment();

        comment.setProblem(problem);
        comment.setText(request.getText());

        // Set creation time because the database column
        // created_at cannot be NULL.
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        // Notify the problem owner when another user
        // adds a comment to their problem.
        if (!problem.getUser().getId().equals(user.getId())) {

            notificationService.createNotification(
                    "A new comment was added to your problem #" + problemId + ".",
                    NotificationType.COMMENT_ADDED,
                    problem.getUser().getEmail()
            );
        }

        return convertToResponse(savedComment);
    }

    public List<CommentResponse> getComments(
            Long problemId,
            String userEmail) {

        Problem problem = getProblem(problemId);
        User user = getUser(userEmail);

        // Check whether the logged-in user is allowed
        // to view comments for this problem.
        validateCommentAccess(problem, user);

        return commentRepository.findByProblemId(problemId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private void validateCommentAccess(
            Problem problem,
            User user) {

        Role role = user.getRole();

        // SUPER_ADMIN can access comments for every problem.
        if (role == Role.SUPER_ADMIN) {
            return;
        }

        // MODERATOR can access comments for every problem.
        if (role == Role.MODERATOR) {
            return;
        }

        // CITIZEN can access comments only for their own problem.
        if (role == Role.CITIZEN) {

            if (!problem.getUser().getId().equals(user.getId())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not allowed to access comments for this problem."
                );
            }

            return;
        }

        // AUTHORITY can access comments only for problems
        // assigned to their authority.
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
                        "You are not allowed to access comments for this problem."
                );
            }

            return;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not allowed to access comments."
        );
    }

    private Problem getProblem(Long problemId) {

        return problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Problem not found with id: " + problemId
                        )
                );
    }

    private User getUser(String userEmail) {

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found."
                        )
                );
    }

    private CommentResponse convertToResponse(Comment comment) {

        CommentResponse response = new CommentResponse();

        response.setId(comment.getId());
        response.setProblemId(comment.getProblem().getId());
        response.setText(comment.getText());
        response.setCreatedAt(comment.getCreatedAt());

        return response;
    }
}
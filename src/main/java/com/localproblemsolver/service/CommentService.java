package com.localproblemsolver.service;

import com.localproblemsolver.dto.CommentRequest;
import com.localproblemsolver.dto.CommentResponse;
import com.localproblemsolver.entity.Comment;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.repository.CommentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProblemRepository problemRepository;

    public CommentService(
            CommentRepository commentRepository,
            ProblemRepository problemRepository) {

        this.commentRepository = commentRepository;
        this.problemRepository = problemRepository;
    }

    public CommentResponse addComment(
            Long problemId,
            CommentRequest request) {

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Problem not found with id: " + problemId
                        )
                );

        Comment comment = new Comment();

        comment.setProblem(problem);
        comment.setText(request.getText());
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return convertToResponse(savedComment);
    }

    public List<CommentResponse> getComments(Long problemId) {

        if (!problemRepository.existsById(problemId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Problem not found with id: " + problemId
            );
        }

        return commentRepository.findByProblemId(problemId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private CommentResponse convertToResponse(Comment comment) {

        return new CommentResponse(
                comment.getId(),
                comment.getProblem().getId(),
                comment.getText(),
                comment.getCreatedAt()
        );
    }
}
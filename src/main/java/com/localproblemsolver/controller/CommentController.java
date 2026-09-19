package com.localproblemsolver.controller;

import com.localproblemsolver.dto.CommentRequest;
import com.localproblemsolver.dto.CommentResponse;
import com.localproblemsolver.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/problems/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public CommentResponse addComment(
            @PathVariable Long problemId,
            @Valid @RequestBody CommentRequest request) {

        return commentService.addComment(problemId, request);
    }

    @GetMapping("/problems/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public List<CommentResponse> getComments(
            @PathVariable Long problemId) {

        return commentService.getComments(problemId);
    }
}
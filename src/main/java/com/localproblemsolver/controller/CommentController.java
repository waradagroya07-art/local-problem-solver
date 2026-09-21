package com.localproblemsolver.controller;

import com.localproblemsolver.dto.CommentRequest;
import com.localproblemsolver.dto.CommentResponse;
import com.localproblemsolver.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/problem/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long problemId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        CommentResponse response =
                commentService.addComment(
                        problemId,
                        request,
                        userEmail
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/problem/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long problemId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        List<CommentResponse> comments =
                commentService.getComments(
                        problemId,
                        userEmail
                );

        return ResponseEntity.ok(comments);
    }
}
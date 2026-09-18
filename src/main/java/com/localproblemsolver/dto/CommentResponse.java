package com.localproblemsolver.dto;

import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private Long problemId;
    private String text;
    private LocalDateTime createdAt;

    public CommentResponse() {
    }

    public CommentResponse(
            Long id,
            Long problemId,
            String text,
            LocalDateTime createdAt) {

        this.id = id;
        this.problemId = problemId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
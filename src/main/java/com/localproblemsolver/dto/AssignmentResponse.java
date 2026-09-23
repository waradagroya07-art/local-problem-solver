package com.localproblemsolver.dto;

import com.localproblemsolver.entity.AssignmentStatus;

import java.time.LocalDateTime;

public class AssignmentResponse {

    private Long id;
    private Long problemId;
    private AuthorityResponse authority;
    private LocalDateTime assignedAt;
    private AssignmentStatus status;

    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public AssignmentResponse() {
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AssignmentResponse(
            Long id,
            Long problemId,
            AuthorityResponse authority,
            LocalDateTime assignedAt,
            AssignmentStatus status) {

        this.id = id;
        this.problemId = problemId;
        this.authority = authority;
        this.assignedAt = assignedAt;
        this.status = status;
    }

    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

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

    public AuthorityResponse getAuthority() {
        return authority;
    }

    public void setAuthority(AuthorityResponse authority) {
        this.authority = authority;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }
}
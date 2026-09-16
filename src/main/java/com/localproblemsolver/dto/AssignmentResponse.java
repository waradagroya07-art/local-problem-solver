package com.localproblemsolver.dto;

import java.time.LocalDateTime;

public class AssignmentResponse {

    private Long id;
    private Long problemId;
    private AuthorityResponse authority;
    private LocalDateTime assignedAt;

    public AssignmentResponse() {
    }

    public AssignmentResponse(
            Long id,
            Long problemId,
            AuthorityResponse authority,
            LocalDateTime assignedAt) {

        this.id = id;
        this.problemId = problemId;
        this.authority = authority;
        this.assignedAt = assignedAt;
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
}
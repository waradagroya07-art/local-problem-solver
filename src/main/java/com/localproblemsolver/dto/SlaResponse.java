package com.localproblemsolver.dto;

import java.time.LocalDateTime;

public class SlaResponse {

    private Long id;
    private Long problemId;
    private String priority;
    private LocalDateTime deadline;
    private boolean breached;

    public SlaResponse() {
    }

    public SlaResponse(
            Long id,
            Long problemId,
            String priority,
            LocalDateTime deadline,
            boolean breached) {

        this.id = id;
        this.problemId = problemId;
        this.priority = priority;
        this.deadline = deadline;
        this.breached = breached;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public boolean isBreached() {
        return breached;
    }

    public void setBreached(boolean breached) {
        this.breached = breached;
    }
}
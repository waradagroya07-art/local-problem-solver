package com.localproblemsolver.dto;

import com.localproblemsolver.entity.ProblemStatus;

import java.time.LocalDateTime;

public class StatusHistoryResponse {

    private Long id;

    private ProblemStatus oldStatus;

    private ProblemStatus newStatus;

    private String changedBy;

    private LocalDateTime changedAt;


    public StatusHistoryResponse() {
    }


    public StatusHistoryResponse(
            Long id,
            ProblemStatus oldStatus,
            ProblemStatus newStatus,
            String changedBy,
            LocalDateTime changedAt) {

        this.id = id;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public ProblemStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(ProblemStatus oldStatus) {
        this.oldStatus = oldStatus;
    }


    public ProblemStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ProblemStatus newStatus) {
        this.newStatus = newStatus;
    }


    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }


    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
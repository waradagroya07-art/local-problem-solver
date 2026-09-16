package com.localproblemsolver.dto;

import com.localproblemsolver.entity.ProblemStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ProblemStatus status;

    public StatusUpdateRequest() {
    }

    public ProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ProblemStatus status) {
        this.status = status;
    }
}
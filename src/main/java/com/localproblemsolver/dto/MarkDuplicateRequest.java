package com.localproblemsolver.dto;

import jakarta.validation.constraints.NotNull;

public class MarkDuplicateRequest {

    @NotNull(message = "Original problem ID is required")
    private Long originalProblemId;

    public MarkDuplicateRequest() {
    }

    public Long getOriginalProblemId() {
        return originalProblemId;
    }

    public void setOriginalProblemId(Long originalProblemId) {
        this.originalProblemId = originalProblemId;
    }
}
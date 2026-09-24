package com.localproblemsolver.dto;

import jakarta.validation.constraints.NotNull;

public class UserAuthorityUpdateRequest {

    @NotNull(message = "Authority id is required")
    private Long authorityId;

    public UserAuthorityUpdateRequest() {
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }
}
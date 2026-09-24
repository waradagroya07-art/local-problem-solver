package com.localproblemsolver.dto;

import com.localproblemsolver.entity.Role;
import jakarta.validation.constraints.NotNull;

public class UserRoleUpdateRequest {

    @NotNull(message = "Role is required")
    private Role role;

    public UserRoleUpdateRequest() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
package com.localproblemsolver.dto;

import com.localproblemsolver.entity.Role;

public class UserRoleUpdateRequest {

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
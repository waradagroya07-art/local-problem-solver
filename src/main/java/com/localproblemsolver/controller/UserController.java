package com.localproblemsolver.controller;

import com.localproblemsolver.dto.UserAuthorityUpdateRequest;
import com.localproblemsolver.dto.UserResponse;
import com.localproblemsolver.dto.UserRoleUpdateRequest;
import com.localproblemsolver.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id);
    }

    @PatchMapping("/{id}/role")
    public UserResponse updateUserRole(
            @PathVariable Long id,
            @RequestBody UserRoleUpdateRequest request) {

        return userService.updateUserRole(id, request);
    }

    @PatchMapping("/{id}/authority")
    public UserResponse updateUserAuthority(
            @PathVariable Long id,
            @RequestBody UserAuthorityUpdateRequest request) {

        return userService.updateUserAuthority(id, request);
    }
}
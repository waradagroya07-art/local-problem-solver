package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AuthorityRequest;
import com.localproblemsolver.dto.AuthorityResponse;
import com.localproblemsolver.service.AuthorityService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/authorities")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminAuthorityController {

    private final AuthorityService authorityService;

    public AdminAuthorityController(
            AuthorityService authorityService) {

        this.authorityService = authorityService;
    }

    @PostMapping
    public AuthorityResponse createAuthority(
            @Valid @RequestBody AuthorityRequest request) {

        return authorityService.createAuthority(request);
    }

    @GetMapping
    public List<AuthorityResponse> getAllAuthorities() {

        return authorityService.getAllAuthorities();
    }
}
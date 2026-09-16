package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AuthorityRequest;
import com.localproblemsolver.dto.AuthorityResponse;
import com.localproblemsolver.service.AuthorityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authorities")
public class AuthorityController {

    private final AuthorityService authorityService;

    public AuthorityController(
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
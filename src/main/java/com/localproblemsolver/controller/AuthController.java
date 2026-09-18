package com.localproblemsolver.controller;

import com.localproblemsolver.dto.RegisterRequest;
import com.localproblemsolver.dto.UserResponse;
import com.localproblemsolver.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @GetMapping("/test")
    public String test() {
        return "Auth Controller is working!";
    }

    @PostMapping("/register")
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }
}
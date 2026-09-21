package com.localproblemsolver.controller;

import com.localproblemsolver.dto.SlaResponse;
import com.localproblemsolver.service.SlaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slas")
public class SlaController {

    private final SlaService slaService;

    public SlaController(SlaService slaService) {
        this.slaService = slaService;
    }

    // =========================================================
    // CREATE SLA
    // =========================================================

    @PostMapping("/problems/{problemId}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'SUPER_ADMIN')")
    public SlaResponse createSla(
            @PathVariable Long problemId) {

        return slaService.createSla(problemId);
    }

    // =========================================================
    // GET SLA FOR A PROBLEM
    // =========================================================

    @GetMapping("/problems/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public SlaResponse getSla(
            @PathVariable Long problemId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return slaService.getSla(
                problemId,
                userEmail
        );
    }

    // =========================================================
    // GET ALL SLAs
    // =========================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'AUTHORITY', 'SUPER_ADMIN')")
    public List<SlaResponse> getAllSlas() {

        return slaService.getAllSlas();
    }
}
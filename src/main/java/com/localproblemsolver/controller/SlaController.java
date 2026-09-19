package com.localproblemsolver.controller;

import com.localproblemsolver.dto.SlaResponse;
import com.localproblemsolver.service.SlaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slas")
public class SlaController {

    private final SlaService slaService;

    public SlaController(SlaService slaService) {
        this.slaService = slaService;
    }

    @PostMapping("/problems/{problemId}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'SUPER_ADMIN')")
    public SlaResponse createSla(
            @PathVariable Long problemId) {

        return slaService.createSla(problemId);
    }

    @GetMapping("/problems/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public SlaResponse getSla(
            @PathVariable Long problemId) {

        return slaService.getSla(problemId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'AUTHORITY', 'SUPER_ADMIN')")
    public List<SlaResponse> getAllSlas() {

        return slaService.getAllSlas();
    }
}
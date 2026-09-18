package com.localproblemsolver.controller;

import com.localproblemsolver.dto.SlaResponse;
import com.localproblemsolver.service.SlaService;
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
    public SlaResponse createSla(
            @PathVariable Long problemId) {

        return slaService.createSla(problemId);
    }

    @GetMapping("/problems/{problemId}")
    public SlaResponse getSla(
            @PathVariable Long problemId) {

        return slaService.getSla(problemId);
    }

    @GetMapping
    public List<SlaResponse> getAllSlas() {

        return slaService.getAllSlas();
    }
}
package com.localproblemsolver.controller;

import com.localproblemsolver.dto.StatusHistoryResponse;
import com.localproblemsolver.service.StatusHistoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
public class StatusHistoryController {

    private final StatusHistoryService statusHistoryService;


    public StatusHistoryController(
            StatusHistoryService statusHistoryService) {

        this.statusHistoryService = statusHistoryService;
    }


    // =========================================================
    // GET STATUS HISTORY
    // =========================================================

    @GetMapping("/{problemId}/status-history")
    @PreAuthorize("isAuthenticated()")
    public List<StatusHistoryResponse> getStatusHistory(
            @PathVariable Long problemId) {

        return statusHistoryService.getStatusHistory(
                problemId
        );
    }
}
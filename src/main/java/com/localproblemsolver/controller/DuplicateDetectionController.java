package com.localproblemsolver.controller;

import com.localproblemsolver.dto.DuplicateResponse;
import com.localproblemsolver.service.DuplicateDetectionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
public class DuplicateDetectionController {

    private final DuplicateDetectionService duplicateDetectionService;

    public DuplicateDetectionController(
            DuplicateDetectionService duplicateDetectionService) {

        this.duplicateDetectionService =
                duplicateDetectionService;
    }

    @GetMapping("/{id}/duplicates")
    @PreAuthorize("hasAnyRole('MODERATOR', 'SUPER_ADMIN')")
    public List<DuplicateResponse> findDuplicates(
            @PathVariable Long id) {

        return duplicateDetectionService.findDuplicates(id);
    }
}
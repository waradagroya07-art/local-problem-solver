package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AttachmentResponse;
import com.localproblemsolver.service.AttachmentService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(
            AttachmentService attachmentService) {

        this.attachmentService = attachmentService;
    }

    @PostMapping(
            value = "/problems/{problemId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    public AttachmentResponse uploadAttachment(
            @PathVariable Long problemId,
            @RequestParam("file") MultipartFile file) {

        return attachmentService.uploadAttachment(
                problemId,
                file
        );
    }

    @GetMapping("/problems/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public List<AttachmentResponse> getAttachments(
            @PathVariable Long problemId) {

        return attachmentService.getAttachments(problemId);
    }
}
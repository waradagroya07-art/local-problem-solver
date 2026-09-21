package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AttachmentResponse;
import com.localproblemsolver.service.AttachmentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    // =========================================================
    // UPLOAD
    // =========================================================

    @PostMapping(
            value = "/problems/{problemId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    public AttachmentResponse uploadAttachment(
            @PathVariable Long problemId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return attachmentService.uploadAttachment(
                problemId,
                file,
                userEmail
        );
    }

    // =========================================================
    // GET ATTACHMENTS
    // =========================================================

    @GetMapping("/problems/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public List<AttachmentResponse> getAttachments(
            @PathVariable Long problemId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return attachmentService.getAttachments(
                problemId,
                userEmail
        );
    }

    // =========================================================
    // DOWNLOAD / VIEW
    // =========================================================

    @GetMapping("/{attachmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long attachmentId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Resource resource =
                attachmentService.downloadAttachment(
                        attachmentId,
                        userEmail
                );

        String contentType =
                MediaType.APPLICATION_OCTET_STREAM_VALUE;

        try {

            String detectedType =
                    resource.getURL()
                            .openConnection()
                            .getContentType();

            if (detectedType != null) {
                contentType = detectedType;
            }

        } catch (Exception ignored) {
            // Use application/octet-stream as fallback
        }

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(contentType)
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                resource.getFilename() +
                                "\""
                )
                .body(resource);
    }
}
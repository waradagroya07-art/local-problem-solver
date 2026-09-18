package com.localproblemsolver.service;

import com.localproblemsolver.dto.AttachmentResponse;
import com.localproblemsolver.entity.Attachment;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.repository.AttachmentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ProblemRepository problemRepository;

    private static final String UPLOAD_DIRECTORY =
            "uploads/attachments";

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            ProblemRepository problemRepository) {

        this.attachmentRepository = attachmentRepository;
        this.problemRepository = problemRepository;
    }

    public AttachmentResponse uploadAttachment(
            Long problemId,
            MultipartFile file) {

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Problem not found with id: " + problemId
                        )
                );

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is required"
            );
        }

        try {

            Path uploadPath =
                    Paths.get(UPLOAD_DIRECTORY);

            Files.createDirectories(uploadPath);

            String originalFileName =
                    file.getOriginalFilename();

            String safeFileName =
                    originalFileName == null
                            ? "file"
                            : Paths.get(originalFileName)
                            .getFileName()
                            .toString();

            String uniqueFileName =
                    UUID.randomUUID()
                            + "_" + safeFileName;

            Path filePath =
                    uploadPath.resolve(uniqueFileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            Attachment attachment = new Attachment();

            attachment.setProblem(problem);
            attachment.setFileName(safeFileName);
            attachment.setFileType(file.getContentType());
            attachment.setFilePath(filePath.toString());
            attachment.setUploadedAt(LocalDateTime.now());

            Attachment savedAttachment =
                    attachmentRepository.save(attachment);

            return convertToResponse(savedAttachment);

        } catch (IOException exception) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to upload file"
            );
        }
    }

    public List<AttachmentResponse> getAttachments(
            Long problemId) {

        if (!problemRepository.existsById(problemId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Problem not found with id: " + problemId
            );
        }

        return attachmentRepository
                .findByProblemId(problemId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private AttachmentResponse convertToResponse(
            Attachment attachment) {

        return new AttachmentResponse(
                attachment.getId(),
                attachment.getProblem().getId(),
                attachment.getFileName(),
                attachment.getFileType(),
                attachment.getFilePath(),
                attachment.getUploadedAt()
        );
    }
}
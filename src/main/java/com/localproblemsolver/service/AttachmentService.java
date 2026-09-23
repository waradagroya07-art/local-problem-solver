package com.localproblemsolver.service;

import com.localproblemsolver.dto.AttachmentResponse;
import com.localproblemsolver.entity.Attachment;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.AttachmentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.UserRepository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;

    private static final String UPLOAD_DIRECTORY =
            "uploads/attachments";

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg",
            ".jpeg",
            ".png",
            ".pdf"
    );


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            ProblemRepository problemRepository,
            UserRepository userRepository,
            AssignmentRepository assignmentRepository) {

        this.attachmentRepository = attachmentRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }


    // =========================================================
    // UPLOAD ATTACHMENT
    // =========================================================

    public AttachmentResponse uploadAttachment(
            Long problemId,
            MultipartFile file,
            String userEmail) {

        Problem problem = findProblem(problemId);

        verifyAccess(problem, userEmail);

        // -----------------------------------------------------
        // Validate file
        // -----------------------------------------------------

        if (file == null || file.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is required"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File size must not exceed 5 MB"
            );
        }

        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null ||
                originalFileName.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid file name"
            );
        }

        // -----------------------------------------------------
        // Make filename safe
        // -----------------------------------------------------

        String safeFileName =
                Paths.get(originalFileName)
                        .getFileName()
                        .toString();

        String extension = "";

        int lastDot =
                safeFileName.lastIndexOf('.');

        if (lastDot >= 0) {

            extension =
                    safeFileName
                            .substring(lastDot)
                            .toLowerCase();
        }

        // -----------------------------------------------------
        // Validate extension
        // -----------------------------------------------------

        if (!ALLOWED_EXTENSIONS.contains(extension)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported file type. Allowed types: JPG, JPEG, PNG, PDF"
            );
        }

        // -----------------------------------------------------
        // Validate content type
        // -----------------------------------------------------

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(
                        contentType.toLowerCase())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid file content type"
            );
        }


        /*
         * Keep track of the physical file path.
         *
         * If database saving fails after the physical file
         * has been created, we can delete the file.
         */
        Path filePath = null;


        try {

            // -------------------------------------------------
            // Create upload directory
            // -------------------------------------------------

            Path uploadPath =
                    Paths.get(UPLOAD_DIRECTORY);

            Files.createDirectories(uploadPath);


            // -------------------------------------------------
            // Generate unique filename
            // -------------------------------------------------

            String uniqueFileName =
                    UUID.randomUUID()
                            + "_" + safeFileName;


            filePath =
                    uploadPath.resolve(uniqueFileName);


            // -------------------------------------------------
            // Save physical file
            // -------------------------------------------------

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );


            // -------------------------------------------------
            // Create Attachment entity
            // -------------------------------------------------

            Attachment attachment =
                    new Attachment();

            attachment.setProblem(problem);

            attachment.setFileName(
                    safeFileName
            );

            attachment.setFileType(
                    contentType
            );

            attachment.setFilePath(
                    filePath.toString()
            );

            attachment.setUploadedAt(
                    LocalDateTime.now()
            );


            // -------------------------------------------------
            // Save database record
            // -------------------------------------------------

            Attachment savedAttachment =
                    attachmentRepository.save(
                            attachment
                    );


            // -------------------------------------------------
            // Return response
            // -------------------------------------------------

            return convertToResponse(
                    savedAttachment
            );


        } catch (Exception exception) {

            /*
             * IMPORTANT:
             *
             * If the physical file was successfully created
             * but the database operation failed, delete the
             * physical file.
             *
             * This prevents orphan files.
             */

            if (filePath != null) {

                try {

                    Files.deleteIfExists(
                            filePath
                    );

                } catch (IOException cleanupException) {

                    /*
                     * Do not replace the original exception
                     * with the cleanup exception.
                     */
                }
            }


            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to upload file"
            );
        }
    }


    // =========================================================
    // GET ATTACHMENTS
    // =========================================================

    public List<AttachmentResponse> getAttachments(
            Long problemId,
            String userEmail) {

        Problem problem =
                findProblem(problemId);

        verifyAccess(
                problem,
                userEmail
        );

        return attachmentRepository
                .findByProblemId(problemId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // DOWNLOAD / VIEW ATTACHMENT
    // =========================================================

    public Resource downloadAttachment(
            Long attachmentId,
            String userEmail) {

        // -----------------------------------------------------
        // 1. Find attachment
        // -----------------------------------------------------

        Attachment attachment =
                attachmentRepository.findById(
                                attachmentId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Attachment not found with id: "
                                                + attachmentId
                                )
                        );


        // -----------------------------------------------------
        // 2. Get problem
        // -----------------------------------------------------

        Problem problem =
                attachment.getProblem();

        if (problem == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Problem associated with attachment not found"
            );
        }


        // -----------------------------------------------------
        // 3. Verify authorization
        // -----------------------------------------------------

        verifyAccess(
                problem,
                userEmail
        );


        try {

            // -------------------------------------------------
            // 4. Get physical file path
            // -------------------------------------------------

            Path filePath =
                    Paths.get(
                            attachment.getFilePath()
                    );


            // -------------------------------------------------
            // 5. Check file exists
            // -------------------------------------------------

            if (!Files.exists(filePath) ||
                    !Files.isRegularFile(filePath)) {

                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Attachment file not found"
                );
            }


            // -------------------------------------------------
            // 6. Convert to Spring Resource
            // -------------------------------------------------

            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );


            // -------------------------------------------------
            // 7. Check resource
            // -------------------------------------------------

            if (!resource.exists() ||
                    !resource.isReadable()) {

                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Attachment file cannot be read"
                );
            }


            return resource;


        } catch (IOException exception) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to read attachment"
            );
        }
    }


    // =========================================================
    // FIND PROBLEM
    // =========================================================

    private Problem findProblem(
            Long problemId) {

        return problemRepository
                .findById(problemId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Problem not found with id: "
                                        + problemId
                        )
                );
    }


    // =========================================================
    // VERIFY USER ACCESS
    // =========================================================

    private void verifyAccess(
            Problem problem,
            String userEmail) {

        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Authenticated user not found"
                                )
                        );


        Role role =
                user.getRole();


        // =====================================================
        // SUPER ADMIN
        // =====================================================

        if (role == Role.SUPER_ADMIN) {

            return;
        }


        // =====================================================
        // MODERATOR
        // =====================================================

        if (role == Role.MODERATOR) {

            return;
        }


        // =====================================================
        // CITIZEN
        // =====================================================

        if (role == Role.CITIZEN) {

            if (problem.getUser() == null ||
                    problem.getUser().getId() == null ||
                    !problem.getUser()
                            .getId()
                            .equals(user.getId())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You can access attachments only for your own problems"
                );
            }

            return;
        }


        // =====================================================
        // AUTHORITY
        // =====================================================

        if (role == Role.AUTHORITY) {

            Authority authority =
                    user.getAuthority();


            if (authority == null ||
                    authority.getId() == null) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Authority is not linked to this user"
                );
            }


            boolean assignedToAuthority =
                    assignmentRepository
                            .existsByProblemIdAndAuthorityId(
                                    problem.getId(),
                                    authority.getId()
                            );


            if (!assignedToAuthority) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "This problem is not assigned to your authority"
                );
            }

            return;
        }


        // =====================================================
        // OTHER ROLES
        // =====================================================

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this attachment"
        );
    }


    // =========================================================
    // CONVERT ENTITY → RESPONSE DTO
    // =========================================================

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
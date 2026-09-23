package com.localproblemsolver.service;

import com.localproblemsolver.dto.AttachmentResponse;
import com.localproblemsolver.entity.Assignment;
import com.localproblemsolver.entity.Attachment;
import com.localproblemsolver.entity.Authority;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.AssignmentRepository;
import com.localproblemsolver.repository.AttachmentRepository;
import com.localproblemsolver.repository.ProblemRepository;
import com.localproblemsolver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @InjectMocks
    private AttachmentService attachmentService;

    private User citizen;
    private User anotherCitizen;
    private User moderator;
    private User superAdmin;
    private User authorityUser;
    private User anotherAuthorityUser;

    private Authority authority;
    private Authority anotherAuthority;

    private Problem citizenProblem;
    private Problem anotherProblem;

    private Path testUploadDirectory;

    @BeforeEach
    void setUp() {

        citizen = new User();
        citizen.setId(1L);
        citizen.setName("Citizen A");
        citizen.setEmail("citizen@gmail.com");
        citizen.setRole(Role.CITIZEN);

        anotherCitizen = new User();
        anotherCitizen.setId(2L);
        anotherCitizen.setName("Citizen B");
        anotherCitizen.setEmail("citizenb@gmail.com");
        anotherCitizen.setRole(Role.CITIZEN);

        moderator = new User();
        moderator.setId(3L);
        moderator.setName("Moderator");
        moderator.setEmail("moderator@gmail.com");
        moderator.setRole(Role.MODERATOR);

        superAdmin = new User();
        superAdmin.setId(4L);
        superAdmin.setName("Super Admin");
        superAdmin.setEmail("admin@gmail.com");
        superAdmin.setRole(Role.SUPER_ADMIN);

        authority = new Authority();
        authority.setId(10L);
        authority.setName("Road Authority");

        anotherAuthority = new Authority();
        anotherAuthority.setId(20L);
        anotherAuthority.setName("Water Authority");

        authorityUser = new User();
        authorityUser.setId(5L);
        authorityUser.setName("Authority User");
        authorityUser.setEmail("authority@gmail.com");
        authorityUser.setRole(Role.AUTHORITY);
        authorityUser.setAuthority(authority);

        anotherAuthorityUser = new User();
        anotherAuthorityUser.setId(6L);
        anotherAuthorityUser.setName("Another Authority");
        anotherAuthorityUser.setEmail("anotherauthority@gmail.com");
        anotherAuthorityUser.setRole(Role.AUTHORITY);
        anotherAuthorityUser.setAuthority(anotherAuthority);

        citizenProblem = new Problem();
        citizenProblem.setId(1L);
        citizenProblem.setTitle("Pothole");
        citizenProblem.setUser(citizen);

        anotherProblem = new Problem();
        anotherProblem.setId(2L);
        anotherProblem.setTitle("Garbage");
        anotherProblem.setUser(anotherCitizen);

        testUploadDirectory = Paths.get("uploads/attachments");
    }

    @AfterEach
    void cleanup() throws IOException {

        if (Files.exists(testUploadDirectory)) {
            try (var files = Files.list(testUploadDirectory)) {
                files.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                    }
                });
            }
        }
    }

    // =========================================================
    // UPLOAD - SUCCESS
    // =========================================================

    @Test
    void citizenCanUploadValidJpgToOwnProblem() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "pothole.jpg",
                "image/jpeg",
                "fake-jpg-content".getBytes()
        );

        Attachment savedAttachment = new Attachment();
        savedAttachment.setId(100L);
        savedAttachment.setProblem(citizenProblem);
        savedAttachment.setFileName("pothole.jpg");
        savedAttachment.setFileType("image/jpeg");
        savedAttachment.setUploadedAt(LocalDateTime.now());

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(attachmentRepository.save(any(Attachment.class)))
                .thenAnswer(invocation -> {
                    Attachment attachment = invocation.getArgument(0);
                    savedAttachment.setFilePath(attachment.getFilePath());
                    return savedAttachment;
                });

        AttachmentResponse result =
                attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1L, result.getProblemId());
        assertEquals("pothole.jpg", result.getFileName());
        assertEquals("image/jpeg", result.getFileType());

        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void citizenCanUploadValidPng() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "road.png",
                "image/png",
                "png-content".getBytes()
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(attachmentRepository.save(any(Attachment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttachmentResponse result =
                attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertEquals("road.png", result.getFileName());
        assertEquals("image/png", result.getFileType());

        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void citizenCanUploadValidPdf() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "evidence.pdf",
                "application/pdf",
                "pdf-content".getBytes()
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(attachmentRepository.save(any(Attachment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttachmentResponse result =
                attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                );

        assertNotNull(result);
        assertEquals("evidence.pdf", result.getFileName());
        assertEquals("application/pdf", result.getFileType());

        verify(attachmentRepository).save(any(Attachment.class));
    }

    // =========================================================
    // FILE VALIDATION
    // =========================================================

    @Test
    void uploadFailsWhenFileIsNull() {

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        1L,
                        null,
                        "citizen@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void uploadFailsWhenFileIsEmpty() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void uploadFailsWhenFileExceeds5MB() {

        byte[] largeData = new byte[5 * 1024 * 1024 + 1];

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeData
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void uploadFailsWhenFilenameIsNull() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                null,
                "image/jpeg",
                "content".getBytes()
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void uploadFailsForUnsupportedExtension() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "malware.exe",
                "application/octet-stream",
                "fake-exe".getBytes()
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void uploadFailsForInvalidContentType() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "application/octet-stream",
                "content".getBytes()
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                )
        );

        assertEquals(400, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    // =========================================================
    // PATH TRAVERSAL / SAFE FILENAME
    // =========================================================

    @Test
    void uploadUsesSafeFilenameInsteadOfPathTraversal() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../secret.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(attachmentRepository.save(any(Attachment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttachmentResponse result =
                attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                );

        assertEquals("secret.jpg", result.getFileName());
    }

    // =========================================================
    // AUTHORIZATION - CITIZEN
    // =========================================================

    @Test
    void citizenCannotUploadToAnotherCitizensProblem() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        2L,
                        file,
                        "citizen@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    // =========================================================
    // AUTHORIZATION - MODERATOR
    // =========================================================

    @Test
    void moderatorCanUploadToAnyProblem() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "review.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        when(attachmentRepository.save(any(Attachment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttachmentResponse result =
                attachmentService.uploadAttachment(
                        2L,
                        file,
                        "moderator@gmail.com"
                );

        assertNotNull(result);
        assertEquals("review.jpg", result.getFileName());

        verify(attachmentRepository).save(any(Attachment.class));
    }

    // =========================================================
    // AUTHORIZATION - SUPER ADMIN
    // =========================================================

    @Test
    void superAdminCanUploadToAnyProblem() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "admin.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(superAdmin));

        when(attachmentRepository.save(any(Attachment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttachmentResponse result =
                attachmentService.uploadAttachment(
                        2L,
                        file,
                        "admin@gmail.com"
                );

        assertNotNull(result);

        verify(attachmentRepository).save(any(Attachment.class));
    }

    // =========================================================
    // AUTHORIZATION - AUTHORITY
    // =========================================================

    @Test
    void assignedAuthorityCanUploadToProblem() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "authority.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.existsByProblemIdAndAuthorityId(
                2L,
                10L
        )).thenReturn(true);

        when(attachmentRepository.save(any(Attachment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AttachmentResponse result =
                attachmentService.uploadAttachment(
                        2L,
                        file,
                        "authority@gmail.com"
                );

        assertNotNull(result);

        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void authorityCannotUploadToUnassignedProblem() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "authority.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.existsByProblemIdAndAuthorityId(
                2L,
                10L
        )).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        2L,
                        file,
                        "authority@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void authorityWithoutLinkedAuthorityCannotUpload() {

        User unlinkedAuthority = new User();
        unlinkedAuthority.setId(7L);
        unlinkedAuthority.setEmail("unlinked@gmail.com");
        unlinkedAuthority.setRole(Role.AUTHORITY);
        unlinkedAuthority.setAuthority(null);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("unlinked@gmail.com"))
                .thenReturn(Optional.of(unlinkedAuthority));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        2L,
                        file,
                        "unlinked@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    // =========================================================
    // PROBLEM / USER NOT FOUND
    // =========================================================

    @Test
    void uploadFailsWhenProblemDoesNotExist() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(problemRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        999L,
                        file,
                        "citizen@gmail.com"
                )
        );

        assertEquals(404, exception.getStatusCode().value());

        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void uploadFailsWhenAuthenticatedUserDoesNotExist() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        1L,
                        file,
                        "unknown@gmail.com"
                )
        );

        assertEquals(401, exception.getStatusCode().value());

        verify(attachmentRepository, never()).save(any());
    }

    // =========================================================
    // GET ATTACHMENTS
    // =========================================================

    @Test
    void citizenCanGetAttachmentsForOwnProblem() {

        Attachment attachment = new Attachment();
        attachment.setId(200L);
        attachment.setProblem(citizenProblem);
        attachment.setFileName("photo.jpg");
        attachment.setFileType("image/jpeg");
        attachment.setFilePath("uploads/attachments/photo.jpg");
        attachment.setUploadedAt(LocalDateTime.now());

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(attachmentRepository.findByProblemId(1L))
                .thenReturn(List.of(attachment));

        List<AttachmentResponse> result =
                attachmentService.getAttachments(
                        1L,
                        "citizen@gmail.com"
                );

        assertEquals(1, result.size());
        assertEquals(200L, result.get(0).getId());
        assertEquals("photo.jpg", result.get(0).getFileName());
        assertEquals("image/jpeg", result.get(0).getFileType());
    }

    @Test
    void citizenCannotGetAttachmentsForAnotherProblem() {

        when(problemRepository.findById(2L))
                .thenReturn(Optional.of(anotherProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.getAttachments(
                        2L,
                        "citizen@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());

        verify(attachmentRepository, never())
                .findByProblemId(anyLong());
    }

    // =========================================================
    // DOWNLOAD
    // =========================================================

    @Test
    void citizenCanDownloadAttachmentFromOwnProblem() throws Exception {

        Path filePath = Files.createTempFile(
                "lps-test-",
                ".jpg"
        );

        Files.writeString(
                filePath,
                "test attachment"
        );

        Attachment attachment = new Attachment();
        attachment.setId(300L);
        attachment.setProblem(citizenProblem);
        attachment.setFileName("photo.jpg");
        attachment.setFileType("image/jpeg");
        attachment.setFilePath(filePath.toString());

        when(attachmentRepository.findById(300L))
                .thenReturn(Optional.of(attachment));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        Resource resource =
                attachmentService.downloadAttachment(
                        300L,
                        "citizen@gmail.com"
                );

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());

        Files.deleteIfExists(filePath);
    }

    @Test
    void downloadFailsWhenAttachmentDoesNotExist() {

        when(attachmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.downloadAttachment(
                        999L,
                        "citizen@gmail.com"
                )
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void downloadFailsWhenAttachmentHasNoProblem() {

        Attachment attachment = new Attachment();
        attachment.setId(301L);
        attachment.setProblem(null);

        when(attachmentRepository.findById(301L))
                .thenReturn(Optional.of(attachment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.downloadAttachment(
                        301L,
                        "citizen@gmail.com"
                )
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void downloadFailsWhenPhysicalFileDoesNotExist() {

        Attachment attachment = new Attachment();
        attachment.setId(302L);
        attachment.setProblem(citizenProblem);
        attachment.setFileName("missing.jpg");
        attachment.setFileType("image/jpeg");
        attachment.setFilePath(
                "uploads/attachments/file-that-does-not-exist.jpg"
        );

        when(attachmentRepository.findById(302L))
                .thenReturn(Optional.of(attachment));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.downloadAttachment(
                        302L,
                        "citizen@gmail.com"
                )
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void unauthorizedCitizenCannotDownloadAttachment() {

        Attachment attachment = new Attachment();
        attachment.setId(303L);
        attachment.setProblem(anotherProblem);
        attachment.setFileName("private.jpg");
        attachment.setFileType("image/jpeg");
        attachment.setFilePath(
                "uploads/attachments/private.jpg"
        );

        when(attachmentRepository.findById(303L))
                .thenReturn(Optional.of(attachment));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.downloadAttachment(
                        303L,
                        "citizen@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    // =========================================================
    // DOWNLOAD AUTHORITY
    // =========================================================

    @Test
    void assignedAuthorityCanDownloadAttachment() throws Exception {

        Path filePath = Files.createTempFile(
                "lps-authority-",
                ".jpg"
        );

        Files.writeString(
                filePath,
                "authority file"
        );

        Attachment attachment = new Attachment();
        attachment.setId(304L);
        attachment.setProblem(anotherProblem);
        attachment.setFileName("authority.jpg");
        attachment.setFileType("image/jpeg");
        attachment.setFilePath(filePath.toString());

        when(attachmentRepository.findById(304L))
                .thenReturn(Optional.of(attachment));

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(authorityUser));

        when(assignmentRepository.existsByProblemIdAndAuthorityId(
                2L,
                10L
        )).thenReturn(true);

        Resource resource =
                attachmentService.downloadAttachment(
                        304L,
                        "authority@gmail.com"
                );

        assertNotNull(resource);
        assertTrue(resource.exists());

        Files.deleteIfExists(filePath);
    }

    @Test
    void wrongAuthorityCannotDownloadAttachment() {

        Attachment attachment = new Attachment();
        attachment.setId(305L);
        attachment.setProblem(anotherProblem);
        attachment.setFileName("private.jpg");
        attachment.setFileType("image/jpeg");
        attachment.setFilePath(
                "uploads/attachments/private.jpg"
        );

        when(attachmentRepository.findById(305L))
                .thenReturn(Optional.of(attachment));

        when(userRepository.findByEmail("anotherauthority@gmail.com"))
                .thenReturn(Optional.of(anotherAuthorityUser));

        when(assignmentRepository.existsByProblemIdAndAuthorityId(
                2L,
                20L
        )).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.downloadAttachment(
                        305L,
                        "anotherauthority@gmail.com"
                )
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    // =========================================================
    // MODERATOR / ADMIN DOWNLOAD
    // =========================================================

    @Test
    void moderatorCanDownloadAnyAttachment() throws Exception {

        Path filePath = Files.createTempFile(
                "lps-moderator-",
                ".jpg"
        );

        Files.writeString(filePath, "moderator file");

        Attachment attachment = new Attachment();
        attachment.setId(306L);
        attachment.setProblem(anotherProblem);
        attachment.setFilePath(filePath.toString());
        attachment.setFileName("moderator.jpg");
        attachment.setFileType("image/jpeg");

        when(attachmentRepository.findById(306L))
                .thenReturn(Optional.of(attachment));

        when(userRepository.findByEmail("moderator@gmail.com"))
                .thenReturn(Optional.of(moderator));

        Resource resource =
                attachmentService.downloadAttachment(
                        306L,
                        "moderator@gmail.com"
                );

        assertNotNull(resource);
        assertTrue(resource.exists());

        Files.deleteIfExists(filePath);
    }

    @Test
    void superAdminCanDownloadAnyAttachment() throws Exception {

        Path filePath = Files.createTempFile(
                "lps-admin-",
                ".jpg"
        );

        Files.writeString(filePath, "admin file");

        Attachment attachment = new Attachment();
        attachment.setId(307L);
        attachment.setProblem(anotherProblem);
        attachment.setFilePath(filePath.toString());
        attachment.setFileName("admin.jpg");
        attachment.setFileType("image/jpeg");

        when(attachmentRepository.findById(307L))
                .thenReturn(Optional.of(attachment));

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(superAdmin));

        Resource resource =
                attachmentService.downloadAttachment(
                        307L,
                        "admin@gmail.com"
                );

        assertNotNull(resource);
        assertTrue(resource.exists());

        Files.deleteIfExists(filePath);
    }

    // =========================================================
    // DATABASE FAILURE / ORPHAN FILE CLEANUP
    // =========================================================

    @Test
    void physicalFileIsCleanedWhenDatabaseSaveFails() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "database-failure.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        when(problemRepository.findById(1L))
                .thenReturn(Optional.of(citizenProblem));

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(citizen));

        when(attachmentRepository.save(any(Attachment.class)))
                .thenThrow(new RuntimeException("Database failure"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.uploadAttachment(
                        1L,
                        file,
                        "citizen@gmail.com"
                )
        );

        assertEquals(500, exception.getStatusCode().value());

        ArgumentCaptor<Attachment> captor =
                ArgumentCaptor.forClass(Attachment.class);

        verify(attachmentRepository).save(captor.capture());

        String path =
                captor.getValue().getFilePath();

        assertFalse(
                Files.exists(Paths.get(path))
        );
    }
}
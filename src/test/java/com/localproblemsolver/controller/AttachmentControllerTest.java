package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AttachmentResponse;
import com.localproblemsolver.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttachmentControllerTest {

    private final AttachmentService service =
            mock(AttachmentService.class);
    private final AttachmentController controller =
            new AttachmentController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void uploadAttachment_usesAuthenticatedEmail() {
        when(authentication.getName()).thenReturn("citizen@gmail.com");

        MultipartFile file = mock(MultipartFile.class);
        AttachmentResponse expected = mock(AttachmentResponse.class);

        when(service.uploadAttachment(
                10L, file, "citizen@gmail.com"))
                .thenReturn(expected);

        AttachmentResponse actual =
                controller.uploadAttachment(
                        10L, file, authentication);

        assertSame(expected, actual);
        verify(service).uploadAttachment(
                10L, file, "citizen@gmail.com");
    }

    @Test
    void getAttachments_usesAuthenticatedEmail() {
        when(authentication.getName()).thenReturn("citizen@gmail.com");

        List<AttachmentResponse> expected =
                List.of(mock(AttachmentResponse.class));

        when(service.getAttachments(
                10L, "citizen@gmail.com"))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getAttachments(10L, authentication)
        );

        verify(service).getAttachments(
                10L, "citizen@gmail.com");
    }

    @Test
    void downloadAttachment_returnsResourceAndHeaders()
            throws Exception {

        when(authentication.getName()).thenReturn("citizen@gmail.com");

        Resource resource = mock(Resource.class);

        when(resource.getFilename()).thenReturn("photo.png");
        when(resource.getURL()).thenReturn(null);

        when(service.downloadAttachment(
                20L, "citizen@gmail.com"))
                .thenReturn(resource);

        ResponseEntity<Resource> result =
                controller.downloadAttachment(
                        20L, authentication);

        assertEquals(200, result.getStatusCode().value());
        assertSame(resource, result.getBody());

        String disposition =
                result.getHeaders().getFirst(
                        "Content-Disposition");

        assertNotNull(disposition);
        assertTrue(disposition.contains("photo.png"));

        verify(service).downloadAttachment(
                20L, "citizen@gmail.com");
    }
}

package com.localproblemsolver.controller;

import com.localproblemsolver.dto.CommentRequest;
import com.localproblemsolver.dto.CommentResponse;
import com.localproblemsolver.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    private final CommentService service =
            mock(CommentService.class);
    private final CommentController controller =
            new CommentController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void addComment_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        CommentRequest request = mock(CommentRequest.class);
        CommentResponse expected = mock(CommentResponse.class);

        when(service.addComment(
                10L,
                request,
                "citizen@gmail.com"))
                .thenReturn(expected);

        ResponseEntity<CommentResponse> result =
                controller.addComment(
                        10L,
                        request,
                        authentication
                );

        assertEquals(200, result.getStatusCode().value());
        assertSame(expected, result.getBody());

        verify(service).addComment(
                10L,
                request,
                "citizen@gmail.com"
        );
    }

    @Test
    void getComments_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        List<CommentResponse> expected =
                List.of(mock(CommentResponse.class));

        when(service.getComments(
                10L,
                "citizen@gmail.com"))
                .thenReturn(expected);

        ResponseEntity<List<CommentResponse>> result =
                controller.getComments(
                        10L,
                        authentication
                );

        assertEquals(200, result.getStatusCode().value());
        assertSame(expected, result.getBody());

        verify(service).getComments(
                10L,
                "citizen@gmail.com"
        );
    }
}

package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AssignmentResponse;
import com.localproblemsolver.service.AssignmentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssignmentControllerTest {

    private final AssignmentService service = mock(AssignmentService.class);
    private final AssignmentController controller =
            new AssignmentController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void assignProblem_usesAuthenticatedEmail() {
        when(authentication.getName()).thenReturn("moderator@gmail.com");

        AssignmentResponse expected = mock(AssignmentResponse.class);
        when(service.assignProblem(10L, "moderator@gmail.com"))
                .thenReturn(expected);

        ResponseEntity<AssignmentResponse> result =
                controller.assignProblem(10L, authentication);

        assertEquals(200, result.getStatusCode().value());
        assertSame(expected, result.getBody());
        verify(service).assignProblem(10L, "moderator@gmail.com");
    }

    @Test
    void acceptAssignment_usesAuthenticatedEmail() {
        when(authentication.getName()).thenReturn("authority@gmail.com");

        AssignmentResponse expected = mock(AssignmentResponse.class);
        when(service.acceptAssignment(10L, "authority@gmail.com"))
                .thenReturn(expected);

        ResponseEntity<AssignmentResponse> result =
                controller.acceptAssignment(10L, authentication);

        assertEquals(200, result.getStatusCode().value());
        assertSame(expected, result.getBody());
        verify(service).acceptAssignment(10L, "authority@gmail.com");
    }

    @Test
    void declineAssignment_usesAuthenticatedEmail() {
        when(authentication.getName()).thenReturn("authority@gmail.com");

        AssignmentResponse expected = mock(AssignmentResponse.class);
        when(service.declineAssignment(10L, "authority@gmail.com"))
                .thenReturn(expected);

        ResponseEntity<AssignmentResponse> result =
                controller.declineAssignment(10L, authentication);

        assertEquals(200, result.getStatusCode().value());
        assertSame(expected, result.getBody());
        verify(service).declineAssignment(10L, "authority@gmail.com");
    }

    @Test
    void reassignProblem_readsAuthorityIdFromRequest() {
        AssignmentResponse expected = mock(AssignmentResponse.class);
        when(service.reassignProblem(10L, 5L)).thenReturn(expected);

        ResponseEntity<AssignmentResponse> result =
                controller.reassignProblem(
                        10L,
                        Map.of("authorityId", 5L)
                );

        assertEquals(200, result.getStatusCode().value());
        assertSame(expected, result.getBody());
        verify(service).reassignProblem(10L, 5L);
    }

    @Test
    void getAssignment_delegatesToService() {
        AssignmentResponse expected = mock(AssignmentResponse.class);
        when(service.getAssignment(10L)).thenReturn(expected);

        ResponseEntity<AssignmentResponse> result =
                controller.getAssignment(10L);

        assertEquals(200, result.getStatusCode().value());
        assertSame(expected, result.getBody());
        verify(service).getAssignment(10L);
    }
}

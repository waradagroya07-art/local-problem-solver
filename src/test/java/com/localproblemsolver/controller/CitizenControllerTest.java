package com.localproblemsolver.controller;

import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.service.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CitizenControllerTest {

    private final ProblemService service =
            mock(ProblemService.class);
    private final CitizenController controller =
            new CitizenController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void confirmProblem_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        Problem problem = mock(Problem.class);
        ProblemResponse expected = mock(ProblemResponse.class);

        when(service.confirmProblem(
                10L, "citizen@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.confirmProblem(
                        10L, authentication)
        );

        verify(service).confirmProblem(
                10L, "citizen@gmail.com");

        verify(service).convertToResponse(problem);
    }

    @Test
    void reopenProblem_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        Problem problem = mock(Problem.class);
        ProblemResponse expected = mock(ProblemResponse.class);

        when(service.reopenProblem(
                10L, "citizen@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.reopenProblem(
                        10L, authentication)
        );

        verify(service).reopenProblem(
                10L, "citizen@gmail.com");

        verify(service).convertToResponse(problem);
    }

    @Test
    void controller_isRestrictedToCitizen() {
        PreAuthorize annotation =
                CitizenController.class.getAnnotation(
                        PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals(
                "hasRole('CITIZEN')",
                annotation.value()
        );
    }
}

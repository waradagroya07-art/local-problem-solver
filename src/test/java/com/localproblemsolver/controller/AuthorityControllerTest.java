package com.localproblemsolver.controller;

import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.service.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorityControllerTest {

    private final ProblemService service =
            mock(ProblemService.class);
    private final AuthorityController controller =
            new AuthorityController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void startProblem_changesStatusToInProgress() {
        when(authentication.getName())
                .thenReturn("authority@gmail.com");

        Problem problem = mock(Problem.class);
        ProblemResponse expected = mock(ProblemResponse.class);

        when(service.changeStatus(
                10L,
                ProblemStatus.IN_PROGRESS,
                "authority@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.startProblem(10L, authentication)
        );

        verify(service).changeStatus(
                10L,
                ProblemStatus.IN_PROGRESS,
                "authority@gmail.com"
        );

        verify(service).convertToResponse(problem);
    }

    @Test
    void resolveProblem_changesStatusToResolved() {
        when(authentication.getName())
                .thenReturn("authority@gmail.com");

        Problem problem = mock(Problem.class);
        ProblemResponse expected = mock(ProblemResponse.class);

        when(service.changeStatus(
                10L,
                ProblemStatus.RESOLVED,
                "authority@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.resolveProblem(10L, authentication)
        );

        verify(service).changeStatus(
                10L,
                ProblemStatus.RESOLVED,
                "authority@gmail.com"
        );

        verify(service).convertToResponse(problem);
    }

    @Test
    void controller_isRestrictedToAuthority() {
        PreAuthorize annotation =
                AuthorityController.class.getAnnotation(
                        PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals(
                "hasRole('AUTHORITY')",
                annotation.value()
        );
    }
}

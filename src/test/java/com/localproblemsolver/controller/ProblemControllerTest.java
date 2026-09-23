package com.localproblemsolver.controller;

import com.localproblemsolver.dto.ProblemRequest;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.dto.StatusUpdateRequest;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.entity.Severity;
import com.localproblemsolver.service.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProblemControllerTest {

    private final ProblemService service =
            mock(ProblemService.class);
    private final ProblemController controller =
            new ProblemController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void createProblem_buildsProblemAndUsesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        ProblemRequest request =
                new ProblemRequest();

        request.setTitle("Broken road");
        request.setDescription("Large pothole");
        request.setSeverity(Severity.HIGH);
        request.setLocation("Nashik Road");
        request.setLatitude(19.9975);
        request.setLongitude(73.7898);
        request.setCategoryId(3L);

        Problem saved = mock(Problem.class);
        ProblemResponse expected =
                mock(ProblemResponse.class);

        when(service.saveProblem(
                any(Problem.class),
                eq(3L),
                eq("citizen@gmail.com")))
                .thenReturn(saved);

        when(service.convertToResponse(saved))
                .thenReturn(expected);

        ProblemResponse actual =
                controller.createProblem(
                        request,
                        authentication);

        assertSame(expected, actual);

        verify(service).saveProblem(
                any(Problem.class),
                eq(3L),
                eq("citizen@gmail.com"));

        verify(service).convertToResponse(saved);
    }

    @Test
    void getAllProblems_delegatesToService() {
        List<ProblemResponse> expected =
                List.of(mock(ProblemResponse.class));

        when(service.findAllProblems())
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getAllProblems()
        );

        verify(service).findAllProblems();
    }

    @Test
    void getProblemById_delegatesToService() {
        ProblemResponse expected =
                mock(ProblemResponse.class);

        when(service.findProblemById(10L))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getProblemById(10L)
        );

        verify(service).findProblemById(10L);
    }

    @Test
    void changeStatus_usesRequestedStatusAndAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("authority@gmail.com");

        StatusUpdateRequest request =
                mock(StatusUpdateRequest.class);

        when(request.getStatus())
                .thenReturn(ProblemStatus.IN_PROGRESS);

        Problem problem = mock(Problem.class);
        ProblemResponse expected =
                mock(ProblemResponse.class);

        when(service.changeStatus(
                10L,
                ProblemStatus.IN_PROGRESS,
                "authority@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.changeStatus(
                        10L,
                        request,
                        authentication)
        );

        verify(service).changeStatus(
                10L,
                ProblemStatus.IN_PROGRESS,
                "authority@gmail.com"
        );
    }

    @Test
    void confirmProblem_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        Problem problem = mock(Problem.class);
        ProblemResponse expected =
                mock(ProblemResponse.class);

        when(service.confirmProblem(
                10L,
                "citizen@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.confirmProblem(
                        10L,
                        authentication)
        );

        verify(service).confirmProblem(
                10L,
                "citizen@gmail.com");
    }

    @Test
    void reopenProblem_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        Problem problem = mock(Problem.class);
        ProblemResponse expected =
                mock(ProblemResponse.class);

        when(service.reopenProblem(
                10L,
                "citizen@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.reopenProblem(
                        10L,
                        authentication)
        );

        verify(service).reopenProblem(
                10L,
                "citizen@gmail.com");
    }
}

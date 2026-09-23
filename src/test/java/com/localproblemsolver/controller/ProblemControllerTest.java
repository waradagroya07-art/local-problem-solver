package com.localproblemsolver.controller;

import com.localproblemsolver.dto.ProblemRequest;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.dto.StatusUpdateRequest;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.service.ProblemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class ProblemControllerTest {

    private ProblemService service;
    private ProblemController controller;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        service = mock(ProblemService.class);
        controller = new ProblemController(service);
        authentication = mock(Authentication.class);
    }


    @Test
    void createProblem_usesAuthenticatedEmail() {

        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        ProblemRequest request =
                mock(ProblemRequest.class);

        when(request.getTitle())
                .thenReturn("Broken Street Light");

        when(request.getDescription())
                .thenReturn("Street light is not working");

        when(request.getSeverity())
                .thenReturn(com.localproblemsolver.entity.Severity.HIGH);

        when(request.getLocation())
                .thenReturn("Nashik");

        when(request.getLatitude())
                .thenReturn(20.0059);

        when(request.getLongitude())
                .thenReturn(73.7910);

        when(request.getCategoryId())
                .thenReturn(1L);

        Problem problem = new Problem();

        Problem saved =
                mock(Problem.class);

        ProblemResponse expected =
                mock(ProblemResponse.class);

        when(service.saveProblem(
                any(Problem.class),
                eq(1L),
                eq("citizen@gmail.com")
        )).thenReturn(saved);

        when(service.convertToResponse(saved))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.createProblem(
                        request,
                        authentication
                )
        );

        verify(service).saveProblem(
                any(Problem.class),
                eq(1L),
                eq("citizen@gmail.com")
        );

        verify(service)
                .convertToResponse(saved);
    }


    @Test
    void getAllProblems_usesAuthenticatedEmail() {

        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        List<ProblemResponse> expected =
                List.of(mock(ProblemResponse.class));

        when(service.findAllProblems(
                "citizen@gmail.com"
        )).thenReturn(expected);

        assertSame(
                expected,
                controller.getAllProblems(authentication)
        );

        verify(service)
                .findAllProblems("citizen@gmail.com");
    }


    @Test
    void getProblemById_usesAuthenticatedEmail() {

        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        ProblemResponse expected =
                mock(ProblemResponse.class);

        when(service.findProblemById(
                10L,
                "citizen@gmail.com"
        )).thenReturn(expected);

        assertSame(
                expected,
                controller.getProblemById(
                        10L,
                        authentication
                )
        );

        verify(service).findProblemById(
                10L,
                "citizen@gmail.com"
        );
    }


    @Test
    void changeStatus_usesRequestedStatusAndAuthenticatedEmail() {

        when(authentication.getName())
                .thenReturn("authority@gmail.com");

        StatusUpdateRequest request =
                mock(StatusUpdateRequest.class);

        when(request.getStatus())
                .thenReturn(ProblemStatus.IN_PROGRESS);

        Problem problem =
                mock(Problem.class);

        ProblemResponse expected =
                mock(ProblemResponse.class);

        when(service.changeStatus(
                10L,
                ProblemStatus.IN_PROGRESS,
                "authority@gmail.com"
        )).thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.changeStatus(
                        10L,
                        request,
                        authentication
                )
        );

        verify(service).changeStatus(
                10L,
                ProblemStatus.IN_PROGRESS,
                "authority@gmail.com"
        );

        verify(service)
                .convertToResponse(problem);
    }
}
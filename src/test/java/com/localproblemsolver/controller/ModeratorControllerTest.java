package com.localproblemsolver.controller;

import com.localproblemsolver.dto.CategoryUpdateRequest;
import com.localproblemsolver.dto.MarkDuplicateRequest;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.service.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModeratorControllerTest {

    private final ProblemService service =
            mock(ProblemService.class);
    private final ModeratorController controller =
            new ModeratorController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void validateProblem_setsValidatedStatus() {
        when(authentication.getName())
                .thenReturn("moderator@gmail.com");

        Problem problem = mock(Problem.class);
        ProblemResponse expected = mock(ProblemResponse.class);

        when(service.changeStatus(
                10L,
                ProblemStatus.VALIDATED,
                "moderator@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.validateProblem(
                        10L,
                        authentication)
        );

        verify(service).changeStatus(
                10L,
                ProblemStatus.VALIDATED,
                "moderator@gmail.com"
        );
    }

    @Test
    void rejectProblem_setsRejectedStatus() {
        when(authentication.getName())
                .thenReturn("moderator@gmail.com");

        Problem problem = mock(Problem.class);
        ProblemResponse expected = mock(ProblemResponse.class);

        when(service.changeStatus(
                10L,
                ProblemStatus.REJECTED,
                "moderator@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.rejectProblem(
                        10L,
                        authentication)
        );

        verify(service).changeStatus(
                10L,
                ProblemStatus.REJECTED,
                "moderator@gmail.com"
        );
    }

    @Test
    void markAsDuplicate_usesOriginalProblemIdAndEmail() {
        when(authentication.getName())
                .thenReturn("moderator@gmail.com");

        MarkDuplicateRequest request =
                mock(MarkDuplicateRequest.class);

        when(request.getOriginalProblemId())
                .thenReturn(20L);

        Problem problem = mock(Problem.class);
        ProblemResponse expected = mock(ProblemResponse.class);

        when(service.markAsDuplicate(
                10L,
                20L,
                "moderator@gmail.com"))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.markAsDuplicate(
                        10L,
                        request,
                        authentication)
        );

        verify(service).markAsDuplicate(
                10L,
                20L,
                "moderator@gmail.com"
        );
    }

    @Test
    void updateCategory_usesCategoryId() {
        CategoryUpdateRequest request =
                mock(CategoryUpdateRequest.class);

        when(request.getCategoryId()).thenReturn(5L);

        Problem problem = mock(Problem.class);
        ProblemResponse expected = mock(ProblemResponse.class);

        when(service.updateCategory(10L, 5L))
                .thenReturn(problem);

        when(service.convertToResponse(problem))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.updateCategory(
                        10L,
                        request)
        );

        verify(service).updateCategory(10L, 5L);
    }

    @Test
    void controller_isRestrictedToModerator() {
        PreAuthorize annotation =
                ModeratorController.class.getAnnotation(
                        PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals(
                "hasRole('MODERATOR')",
                annotation.value()
        );
    }
}

package com.localproblemsolver.controller;

import com.localproblemsolver.dto.StatusHistoryResponse;
import com.localproblemsolver.service.StatusHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatusHistoryControllerTest {

    private final StatusHistoryService service =
            mock(StatusHistoryService.class);
    private final StatusHistoryController controller =
            new StatusHistoryController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void getStatusHistory_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        List<StatusHistoryResponse> expected =
                List.of(mock(StatusHistoryResponse.class));

        when(service.getStatusHistory(
                10L,
                "citizen@gmail.com"))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getStatusHistory(
                        10L,
                        authentication)
        );

        verify(service).getStatusHistory(
                10L,
                "citizen@gmail.com");
    }
}

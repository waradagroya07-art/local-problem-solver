package com.localproblemsolver.controller;

import com.localproblemsolver.dto.SlaResponse;
import com.localproblemsolver.service.SlaService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlaControllerTest {

    private final SlaService service =
            mock(SlaService.class);
    private final SlaController controller =
            new SlaController(service);
    private final Authentication authentication =
            mock(Authentication.class);

    @Test
    void createSla_delegatesToService() {
        SlaResponse expected =
                mock(SlaResponse.class);

        when(service.createSla(10L))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.createSla(10L)
        );

        verify(service).createSla(10L);
    }

    @Test
    void getSla_usesAuthenticatedEmail() {
        when(authentication.getName())
                .thenReturn("citizen@gmail.com");

        SlaResponse expected =
                mock(SlaResponse.class);

        when(service.getSla(
                10L,
                "citizen@gmail.com"))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getSla(
                        10L,
                        authentication)
        );

        verify(service).getSla(
                10L,
                "citizen@gmail.com");
    }

    @Test
    void getAllSlas_delegatesToService() {
        List<SlaResponse> expected =
                List.of(mock(SlaResponse.class));

        when(service.getAllSlas())
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getAllSlas()
        );

        verify(service).getAllSlas();
    }
}

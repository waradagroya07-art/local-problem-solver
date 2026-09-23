package com.localproblemsolver.controller;

import com.localproblemsolver.dto.AuthorityRequest;
import com.localproblemsolver.dto.AuthorityResponse;
import com.localproblemsolver.service.AuthorityService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminAuthorityControllerTest {

    private final AuthorityService service = mock(AuthorityService.class);
    private final AdminAuthorityController controller =
            new AdminAuthorityController(service);

    @Test
    void createAuthority_delegatesToService() {
        AuthorityRequest request = mock(AuthorityRequest.class);
        AuthorityResponse expected = mock(AuthorityResponse.class);

        when(service.createAuthority(request)).thenReturn(expected);

        AuthorityResponse actual = controller.createAuthority(request);

        assertSame(expected, actual);
        verify(service).createAuthority(request);
    }

    @Test
    void getAllAuthorities_delegatesToService() {
        List<AuthorityResponse> expected =
                List.of(mock(AuthorityResponse.class));

        when(service.getAllAuthorities()).thenReturn(expected);

        assertSame(expected, controller.getAllAuthorities());
        verify(service).getAllAuthorities();
    }

    @Test
    void controller_isRestrictedToSuperAdmin() {
        PreAuthorize annotation =
                AdminAuthorityController.class.getAnnotation(PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals("hasRole('SUPER_ADMIN')", annotation.value());
    }
}

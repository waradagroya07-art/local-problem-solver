package com.localproblemsolver.controller;

import com.localproblemsolver.dto.RegisterRequest;
import com.localproblemsolver.dto.UserResponse;
import com.localproblemsolver.service.AuthService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private final AuthService service = mock(AuthService.class);
    private final AuthController controller =
            new AuthController(service);

    @Test
    void register_delegatesToAuthService() {
        RegisterRequest request = mock(RegisterRequest.class);
        UserResponse expected = mock(UserResponse.class);

        when(service.register(request)).thenReturn(expected);

        assertSame(expected, controller.register(request));
        verify(service).register(request);
    }
}

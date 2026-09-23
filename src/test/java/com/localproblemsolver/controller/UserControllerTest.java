package com.localproblemsolver.controller;

import com.localproblemsolver.dto.UserAuthorityUpdateRequest;
import com.localproblemsolver.dto.UserResponse;
import com.localproblemsolver.dto.UserRoleUpdateRequest;
import com.localproblemsolver.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private final UserService service =
            mock(UserService.class);
    private final UserController controller =
            new UserController(service);

    @Test
    void getAllUsers_delegatesToService() {
        List<UserResponse> expected =
                List.of(mock(UserResponse.class));

        when(service.getAllUsers())
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getAllUsers()
        );

        verify(service).getAllUsers();
    }

    @Test
    void getUserById_delegatesToService() {
        UserResponse expected =
                mock(UserResponse.class);

        when(service.getUserById(10L))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.getUserById(10L)
        );

        verify(service).getUserById(10L);
    }

    @Test
    void updateUserRole_delegatesToService() {
        UserRoleUpdateRequest request =
                mock(UserRoleUpdateRequest.class);

        UserResponse expected =
                mock(UserResponse.class);

        when(service.updateUserRole(
                10L,
                request))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.updateUserRole(
                        10L,
                        request)
        );

        verify(service).updateUserRole(
                10L,
                request);
    }

    @Test
    void updateUserAuthority_delegatesToService() {
        UserAuthorityUpdateRequest request =
                mock(UserAuthorityUpdateRequest.class);

        UserResponse expected =
                mock(UserResponse.class);

        when(service.updateUserAuthority(
                10L,
                request))
                .thenReturn(expected);

        assertSame(
                expected,
                controller.updateUserAuthority(
                        10L,
                        request)
        );

        verify(service).updateUserAuthority(
                10L,
                request);
    }

    @Test
    void controller_isRestrictedToSuperAdmin() {
        PreAuthorize annotation =
                UserController.class.getAnnotation(
                        PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals(
                "hasRole('SUPER_ADMIN')",
                annotation.value()
        );
    }
}

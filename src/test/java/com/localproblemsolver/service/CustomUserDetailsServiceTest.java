package com.localproblemsolver.service;

import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Citizen A");
        user.setEmail("citizen@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CITIZEN);
    }

    @Test
    void loadUserByUsernameSuccessfully() {

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("citizen@gmail.com");

        assertNotNull(result);
        assertEquals(
                "citizen@gmail.com",
                result.getUsername()
        );
        assertEquals(
                "encodedPassword",
                result.getPassword()
        );

        verify(userRepository)
                .findByEmail("citizen@gmail.com");
    }

    @Test
    void loadUserByUsernameAssignsCorrectCitizenRole() {

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("citizen@gmail.com");

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_CITIZEN")
                        )
        );
    }

    @Test
    void loadUserByUsernameAssignsAuthorityRole() {

        user.setRole(Role.AUTHORITY);

        when(userRepository.findByEmail("authority@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("authority@gmail.com");

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_AUTHORITY")
                        )
        );
    }

    @Test
    void loadUserByUsernameAssignsModeratorRole() {

        user.setRole(Role.MODERATOR);

        when(userRepository.findByEmail("moderator@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("moderator@gmail.com");

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_MODERATOR")
                        )
        );
    }

    @Test
    void loadUserByUsernameAssignsSuperAdminRole() {

        user.setRole(Role.SUPER_ADMIN);

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("admin@gmail.com");

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_SUPER_ADMIN")
                        )
        );
    }

    @Test
    void loadUserByUsernameFailsWhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> service.loadUserByUsername(
                                "unknown@gmail.com"
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains("unknown@gmail.com")
        );

        verify(userRepository)
                .findByEmail("unknown@gmail.com");
    }

    @Test
    void loadUserByUsernameCreatesOnlyOneAuthority() {

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("citizen@gmail.com");

        assertEquals(
                1,
                result.getAuthorities().size()
        );
    }

    @Test
    void loadUserByUsernameUsesStoredPassword() {

        when(userRepository.findByEmail("citizen@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("citizen@gmail.com");

        assertEquals(
                user.getPassword(),
                result.getPassword()
        );
    }
}
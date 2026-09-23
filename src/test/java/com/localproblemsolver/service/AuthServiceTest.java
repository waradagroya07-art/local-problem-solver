package com.localproblemsolver.service;

import com.localproblemsolver.dto.RegisterRequest;
import com.localproblemsolver.dto.UserResponse;
import com.localproblemsolver.entity.Role;
import com.localproblemsolver.entity.User;
import com.localproblemsolver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {

        request = new RegisterRequest();

        request.setName("Gourav");
        request.setEmail("gourav@gmail.com");
        request.setPassword("password123");
    }

    // =========================================================
    // SUCCESSFUL REGISTRATION
    // =========================================================

    @Test
    void registerSuccessfully() {

        when(userRepository.existsByEmail("gourav@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setName("Gourav");
        savedUser.setEmail("gourav@gmail.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.CITIZEN);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponse result =
                authService.register(request);

        assertNotNull(result);

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "Gourav",
                result.getName()
        );

        assertEquals(
                "gourav@gmail.com",
                result.getEmail()
        );

        assertEquals(
                Role.CITIZEN,
                result.getRole()
        );

        verify(userRepository)
                .existsByEmail("gourav@gmail.com");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }

    // =========================================================
    // DUPLICATE EMAIL
    // =========================================================

    @Test
    void registerFailsWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail("gourav@gmail.com"))
                .thenReturn(true);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                400,
                exception.getStatusCode().value()
        );

        assertTrue(
                exception.getReason()
                        .contains("Email already registered")
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    // =========================================================
    // PASSWORD IS ENCODED
    // =========================================================

    @Test
    void registerEncodesPasswordBeforeSaving() {

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        authService.register(request);

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(argThat(user ->
                        user.getPassword()
                                .equals("hashedPassword")
                ));
    }

    // =========================================================
    // PASSWORD MUST NOT BE STORED AS PLAIN TEXT
    // =========================================================

    @Test
    void registerDoesNotStorePlainTextPassword() {

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        authService.register(request);

        verify(userRepository)
                .save(argThat(user ->
                        !user.getPassword()
                                .equals("password123")
                ));
    }

    // =========================================================
    // NORMAL REGISTRATION CREATES CITIZEN
    // =========================================================

    @Test
    void registerCreatesCitizenRole() {

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UserResponse result =
                authService.register(request);

        assertEquals(
                Role.CITIZEN,
                result.getRole()
        );

        verify(userRepository)
                .save(argThat(user ->
                        user.getRole() == Role.CITIZEN
                ));
    }

    // =========================================================
    // VERIFY USER DATA BEFORE SAVE
    // =========================================================

    @Test
    void registerBuildsUserWithCorrectData() {

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        authService.register(request);

        verify(userRepository)
                .save(argThat(user ->
                        user.getName().equals("Gourav")
                                && user.getEmail()
                                .equals("gourav@gmail.com")
                                && user.getPassword()
                                .equals("encodedPassword")
                                && user.getRole()
                                == Role.CITIZEN
                ));
    }

    // =========================================================
    // RESPONSE DATA
    // =========================================================

    @Test
    void registerReturnsSavedUserData() {

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        User savedUser = new User();

        savedUser.setId(50L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@gmail.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.CITIZEN);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        request.setName("Test User");
        request.setEmail("test@gmail.com");

        UserResponse result =
                authService.register(request);

        assertEquals(
                50L,
                result.getId()
        );

        assertEquals(
                "Test User",
                result.getName()
        );

        assertEquals(
                "test@gmail.com",
                result.getEmail()
        );

        assertEquals(
                Role.CITIZEN,
                result.getRole()
        );
    }

    // =========================================================
    // REPOSITORY SAVE CALLED ONLY ONCE
    // =========================================================

    @Test
    void registerSavesUserOnlyOnce() {

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        authService.register(request);

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    // =========================================================
    // PASSWORD ENCODER CALLED ONLY ONCE
    // =========================================================

    @Test
    void registerEncodesPasswordOnlyOnce() {

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        authService.register(request);

        verify(passwordEncoder, times(1))
                .encode("password123");
    }
}
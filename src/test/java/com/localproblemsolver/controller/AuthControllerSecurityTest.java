package com.localproblemsolver.controller;

import com.localproblemsolver.config.SecurityConfig;
import com.localproblemsolver.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import com.localproblemsolver.dto.RegisterRequest;
import com.localproblemsolver.dto.UserResponse;
import com.localproblemsolver.service.AuthService;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean AuthService authService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    private static final String BODY="{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"test123\"}";
    @Test void registrationIsPublic_is200() throws Exception { when(authService.register(any(RegisterRequest.class))).thenReturn(new UserResponse()); mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void authenticatedCitizenCanStillRegister_is200() throws Exception { when(authService.register(any(RegisterRequest.class))).thenReturn(new UserResponse()); mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isOk()); }
    @Test void invalidRegistration_is400() throws Exception { mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\",\"email\":\"bad\",\"password\":\"\"}")).andExpect(status().isBadRequest()); }
}

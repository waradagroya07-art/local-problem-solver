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

import com.localproblemsolver.service.StatusHistoryService;

@WebMvcTest(controllers = StatusHistoryController.class)
@Import(SecurityConfig.class)
class StatusHistoryControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean StatusHistoryService statusHistoryService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    @Test @WithMockUser(roles="CITIZEN") void authenticatedCitizenCanReadHistory_is200() throws Exception { when(statusHistoryService.getStatusHistory(1L, "user")).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/problems/1/status-history")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authenticatedAuthorityCanReadHistory_is200() throws Exception { when(statusHistoryService.getStatusHistory(1L, "user")).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/problems/1/status-history")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="MODERATOR") void authenticatedModeratorCanReadHistory_is200() throws Exception { when(statusHistoryService.getStatusHistory(1L, "user")).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/problems/1/status-history")).andExpect(status().isOk()); }
    @Test void unauthenticatedReadHistory_is401() throws Exception { mockMvc.perform(get("/api/problems/1/status-history")).andExpect(status().isUnauthorized()); }
}

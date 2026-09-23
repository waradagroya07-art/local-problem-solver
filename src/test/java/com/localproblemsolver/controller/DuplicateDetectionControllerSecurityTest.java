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

import com.localproblemsolver.service.DuplicateDetectionService;

@WebMvcTest(controllers = DuplicateDetectionController.class)
@Import(SecurityConfig.class)
class DuplicateDetectionControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean DuplicateDetectionService duplicateDetectionService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    @Test @WithMockUser(roles="CITIZEN") void authenticatedCitizenCanRequestDuplicates_is200() throws Exception { when(duplicateDetectionService.findDuplicates(1L)).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/problems/1/duplicates")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authenticatedAuthorityCanRequestDuplicates_is200() throws Exception { when(duplicateDetectionService.findDuplicates(1L)).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/problems/1/duplicates")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="MODERATOR") void authenticatedModeratorCanRequestDuplicates_is200() throws Exception { when(duplicateDetectionService.findDuplicates(1L)).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/problems/1/duplicates")).andExpect(status().isOk()); }
    @Test void unauthenticatedRequest_is401() throws Exception { mockMvc.perform(get("/api/problems/1/duplicates")).andExpect(status().isUnauthorized()); }
}

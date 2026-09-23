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

import com.localproblemsolver.dto.SlaResponse;
import com.localproblemsolver.service.SlaService;

@WebMvcTest(controllers = SlaController.class)
@Import(SecurityConfig.class)
class SlaControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean SlaService slaService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    private final SlaResponse response = new SlaResponse();
    @Test @WithMockUser(roles="MODERATOR") void moderatorCanCreateSla_is200() throws Exception { when(slaService.createSla(1L)).thenReturn(response); mockMvc.perform(post("/api/slas/problems/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCanCreateSla_is200() throws Exception { when(slaService.createSla(1L)).thenReturn(response); mockMvc.perform(post("/api/slas/problems/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotCreateSla_is403() throws Exception { mockMvc.perform(post("/api/slas/problems/1")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCannotCreateSla_is403() throws Exception { mockMvc.perform(post("/api/slas/problems/1")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCanReadAssignedOrAccessibleSlaEndpoint_is200() throws Exception { when(slaService.getSla(1L, "user")).thenReturn(response); mockMvc.perform(get("/api/slas/problems/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCanReadOwnSlaEndpoint_is200() throws Exception { when(slaService.getSla(1L, "user")).thenReturn(response); mockMvc.perform(get("/api/slas/problems/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCanGetAllSlas_is200() throws Exception { when(slaService.getAllSlas()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/slas")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCanGetAllSlas_is200() throws Exception { when(slaService.getAllSlas()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/slas")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCanGetAllSlas_is200() throws Exception { when(slaService.getAllSlas()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/slas")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotGetAllSlas_is403() throws Exception { mockMvc.perform(get("/api/slas")).andExpect(status().isForbidden()); }
    @Test void unauthenticatedGetSla_is401() throws Exception { mockMvc.perform(get("/api/slas/problems/1")).andExpect(status().isUnauthorized()); }
}

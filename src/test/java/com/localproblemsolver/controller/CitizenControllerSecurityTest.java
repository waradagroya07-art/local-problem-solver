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

import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.service.ProblemService;

@WebMvcTest(controllers = CitizenController.class)
@Import(SecurityConfig.class)
class CitizenControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean ProblemService problemService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    private void stub(String action) { Problem p=new Problem(); if(action.equals("confirm")) when(problemService.confirmProblem(1L,"user")).thenReturn(p); else when(problemService.reopenProblem(1L,"user")).thenReturn(p); when(problemService.convertToResponse(p)).thenReturn(new ProblemResponse()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCanConfirm_is200() throws Exception { stub("confirm"); mockMvc.perform(patch("/api/citizen/problems/1/confirm")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCanReopen_is200() throws Exception { stub("reopen"); mockMvc.perform(patch("/api/citizen/problems/1/reopen")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCannotConfirm_is403() throws Exception { mockMvc.perform(patch("/api/citizen/problems/1/confirm")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCannotReopen_is403() throws Exception { mockMvc.perform(patch("/api/citizen/problems/1/reopen")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCannotConfirm_is403() throws Exception { mockMvc.perform(patch("/api/citizen/problems/1/confirm")).andExpect(status().isForbidden()); }
    @Test void unauthenticatedConfirm_is401() throws Exception { mockMvc.perform(patch("/api/citizen/problems/1/confirm")).andExpect(status().isUnauthorized()); }
}

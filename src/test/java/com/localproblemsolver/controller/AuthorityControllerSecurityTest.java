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

@WebMvcTest(controllers = AuthorityController.class)
@Import(SecurityConfig.class)
class AuthorityControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean ProblemService problemService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    private void stubProblem(){ Problem p=new Problem(); when(problemService.changeStatus(anyLong(),any(),anyString())).thenReturn(p); when(problemService.convertToResponse(p)).thenReturn(new ProblemResponse()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCanStart_is200() throws Exception { stubProblem(); mockMvc.perform(patch("/api/authority/problems/1/start")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCanResolve_is200() throws Exception { stubProblem(); mockMvc.perform(patch("/api/authority/problems/1/resolve")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotStart_is403() throws Exception { mockMvc.perform(patch("/api/authority/problems/1/start")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCannotResolve_is403() throws Exception { mockMvc.perform(patch("/api/authority/problems/1/resolve")).andExpect(status().isForbidden()); }
    @Test void unauthenticatedStart_is401() throws Exception { mockMvc.perform(patch("/api/authority/problems/1/start")).andExpect(status().isUnauthorized()); }
}

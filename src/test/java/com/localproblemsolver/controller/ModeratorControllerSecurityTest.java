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
import com.localproblemsolver.service.ProblemService;
import com.localproblemsolver.entity.Problem;

@WebMvcTest(controllers = ModeratorController.class)
@Import(SecurityConfig.class)
class ModeratorControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean ProblemService problemService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    private void stubProblem() { Problem p=new Problem(); when(problemService.changeStatus(anyLong(),any(),anyString())).thenReturn(p); when(problemService.convertToResponse(p)).thenReturn(new ProblemResponse()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCanValidate_is200() throws Exception { stubProblem(); mockMvc.perform(patch("/api/moderator/problems/1/validate")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCannotValidate_is403() throws Exception { mockMvc.perform(patch("/api/moderator/problems/1/validate")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotReject_is403() throws Exception { mockMvc.perform(patch("/api/moderator/problems/1/reject")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCanReject_is200() throws Exception { stubProblem(); mockMvc.perform(patch("/api/moderator/problems/1/reject")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCannotMarkDuplicate_is403() throws Exception { mockMvc.perform(patch("/api/moderator/problems/1/duplicate").contentType(MediaType.APPLICATION_JSON).content("{\"originalProblemId\":2}")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotUpdateCategory_is403() throws Exception { mockMvc.perform(patch("/api/moderator/problems/1/category").contentType(MediaType.APPLICATION_JSON).content("{\"categoryId\":2}")).andExpect(status().isForbidden()); }
    @Test void unauthenticatedValidate_is401() throws Exception { mockMvc.perform(patch("/api/moderator/problems/1/validate")).andExpect(status().isUnauthorized()); }
}

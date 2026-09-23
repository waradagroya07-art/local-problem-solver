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

import com.localproblemsolver.dto.AssignmentResponse;
import com.localproblemsolver.service.AssignmentService;
import java.util.Map;

@WebMvcTest(controllers = AssignmentController.class)
@Import(SecurityConfig.class)
class AssignmentControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean AssignmentService assignmentService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    private final AssignmentResponse response = new AssignmentResponse();

    @Test @WithMockUser(roles="MODERATOR") void moderatorCanAssign_is200() throws Exception { when(assignmentService.assignProblem(1L,"user")).thenReturn(response); mockMvc.perform(post("/api/assignments/problems/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotAssign_is403() throws Exception { mockMvc.perform(post("/api/assignments/problems/1")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCannotAssign_is403() throws Exception { mockMvc.perform(post("/api/assignments/problems/1")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCanAccept_is200() throws Exception { when(assignmentService.acceptAssignment(1L,"user")).thenReturn(response); mockMvc.perform(put("/api/assignments/problems/1/accept")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotAccept_is403() throws Exception { mockMvc.perform(put("/api/assignments/problems/1/accept")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCanDecline_is200() throws Exception { when(assignmentService.declineAssignment(1L,"user")).thenReturn(response); mockMvc.perform(put("/api/assignments/problems/1/decline")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCannotDecline_is403() throws Exception { mockMvc.perform(put("/api/assignments/problems/1/decline")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCanReassign_is200() throws Exception { when(assignmentService.reassignProblem(1L,2L)).thenReturn(response); mockMvc.perform(put("/api/assignments/problems/1/reassign").contentType(MediaType.APPLICATION_JSON).content("{\"authorityId\":2}")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCannotReassign_is403() throws Exception { mockMvc.perform(put("/api/assignments/problems/1/reassign").contentType(MediaType.APPLICATION_JSON).content("{\"authorityId\":2}")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCanGetAssignment_is200() throws Exception { when(assignmentService.getAssignment(1L)).thenReturn(response); mockMvc.perform(get("/api/assignments/problems/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotGetAssignment_is403() throws Exception { mockMvc.perform(get("/api/assignments/problems/1")).andExpect(status().isForbidden()); }
    @Test void unauthenticatedAssign_is401() throws Exception { mockMvc.perform(post("/api/assignments/problems/1")).andExpect(status().isUnauthorized()); }
}

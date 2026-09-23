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

import com.localproblemsolver.dto.CommentRequest;
import com.localproblemsolver.dto.CommentResponse;
import com.localproblemsolver.service.CommentService;

@WebMvcTest(controllers = CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean CommentService commentService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    private static final String BODY = "{\"text\":\"Investigating this issue\"}";
    @Test @WithMockUser(roles="CITIZEN") void citizenCanAddComment_is200() throws Exception { when(commentService.addComment(eq(1L), any(CommentRequest.class), anyString())).thenReturn(new CommentResponse()); mockMvc.perform(post("/api/comments/problem/1").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCanGetComments_is200() throws Exception { when(commentService.getComments(1L, "user")).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/comments/problem/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCanAddComment_is200() throws Exception { when(commentService.addComment(eq(1L), any(CommentRequest.class), anyString())).thenReturn(new CommentResponse()); mockMvc.perform(post("/api/comments/problem/1").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isOk()); }
    @Test void unauthenticatedAddComment_is401() throws Exception { mockMvc.perform(post("/api/comments/problem/1").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized()); }
    @Test void unauthenticatedGetComments_is401() throws Exception { mockMvc.perform(get("/api/comments/problem/1")).andExpect(status().isUnauthorized()); }
}

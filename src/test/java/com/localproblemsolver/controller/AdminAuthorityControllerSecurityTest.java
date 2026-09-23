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

import com.localproblemsolver.dto.AuthorityResponse;
import com.localproblemsolver.service.AuthorityService;

@WebMvcTest(controllers = AdminAuthorityController.class)
@Import(SecurityConfig.class)
class AdminAuthorityControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean AuthorityService authorityService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    private static final String BODY="{\"name\":\"Road Department\",\"categoryId\":1,\"zone\":\"Pune\"}";
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCanCreateAuthority_is200() throws Exception { when(authorityService.createAuthority(any())).thenReturn(mock(AuthorityResponse.class)); mockMvc.perform(post("/api/admin/authorities").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCanGetAuthorities_is200() throws Exception { when(authorityService.getAllAuthorities()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/admin/authorities")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotCreateAuthority_is403() throws Exception { mockMvc.perform(post("/api/admin/authorities").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCannotGetAuthorities_is403() throws Exception { mockMvc.perform(get("/api/admin/authorities")).andExpect(status().isForbidden()); }
    @Test void unauthenticatedGetAuthorities_is401() throws Exception { mockMvc.perform(get("/api/admin/authorities")).andExpect(status().isUnauthorized()); }
}

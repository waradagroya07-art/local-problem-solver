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

import com.localproblemsolver.service.UserService;

@WebMvcTest(controllers = UserController.class)
@Import(SecurityConfig.class)
class UserControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean UserService userService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCanListUsers_is200() throws Exception { when(userService.getAllUsers()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/admin/users")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCanGetUser_is200() throws Exception { when(userService.getUserById(1L)).thenReturn(new com.localproblemsolver.dto.UserResponse()); mockMvc.perform(get("/api/admin/users/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCanUpdateRole_is200() throws Exception { when(userService.updateUserRole(eq(1L), any())).thenReturn(new com.localproblemsolver.dto.UserResponse()); mockMvc.perform(patch("/api/admin/users/1/role").contentType("application/json").content("{}" )).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotListUsers_is403() throws Exception { mockMvc.perform(get("/api/admin/users")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCannotUpdateAuthority_is403() throws Exception { mockMvc.perform(patch("/api/admin/users/1/authority").contentType("application/json").content("{}" )).andExpect(status().isForbidden()); }
    @Test void unauthenticatedListUsers_is401() throws Exception { mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized()); }
}

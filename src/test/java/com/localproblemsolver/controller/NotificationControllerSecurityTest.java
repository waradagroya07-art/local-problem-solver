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

import com.localproblemsolver.dto.NotificationResponse;
import com.localproblemsolver.entity.NotificationType;
import com.localproblemsolver.service.NotificationService;

@WebMvcTest(controllers = NotificationController.class)
@Import(SecurityConfig.class)
class NotificationControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean NotificationService notificationService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCanCreateNotification_is200() throws Exception { when(notificationService.createNotification("Hello",NotificationType.STATUS_CHANGED,"user")).thenReturn(new NotificationResponse()); mockMvc.perform(post("/api/notifications").param("message","Hello").param("type","STATUS_CHANGED")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotCreateNotification_is403() throws Exception { mockMvc.perform(post("/api/notifications").param("message","Hello").param("type","STATUS_CHANGED")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCanGetMyNotifications_is200() throws Exception { when(notificationService.getMyNotifications("user")).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/notifications")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCanGetNotification_is200() throws Exception { when(notificationService.getNotification(1L,"user")).thenReturn(new NotificationResponse()); mockMvc.perform(get("/api/notifications/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCanMarkNotificationRead_is200() throws Exception { when(notificationService.markAsRead(1L,"user")).thenReturn(new NotificationResponse()); mockMvc.perform(patch("/api/notifications/1/read")).andExpect(status().isOk()); }
    @Test void unauthenticatedGetNotifications_is401() throws Exception { mockMvc.perform(get("/api/notifications")).andExpect(status().isUnauthorized()); }
}

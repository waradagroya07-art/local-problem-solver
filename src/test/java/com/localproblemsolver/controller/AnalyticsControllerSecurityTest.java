package com.localproblemsolver.controller;

import com.localproblemsolver.config.SecurityConfig;
import com.localproblemsolver.service.AnalyticsService;
import com.localproblemsolver.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = AnalyticsController.class)
@Import(SecurityConfig.class)
class AnalyticsControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean AnalyticsService analyticsService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;

    @Test @WithMockUser(roles="SUPER_ADMIN") void overview_superAdmin_is200() throws Exception { when(analyticsService.getOverview()).thenReturn(null); mockMvc.perform(get("/api/analytics/overview")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void categories_superAdmin_is200() throws Exception { when(analyticsService.getCategoryAnalytics()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/analytics/categories")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void severity_superAdmin_is200() throws Exception { when(analyticsService.getSeverityAnalytics()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/analytics/severity")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void priority_superAdmin_is200() throws Exception { when(analyticsService.getPriorityAnalytics()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/analytics/priority")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void trends_superAdmin_is200() throws Exception { when(analyticsService.getTrendAnalytics(any(), any())).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/analytics/trends").param("from","2026-01-01").param("to","2026-09-01")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void sla_superAdmin_is200() throws Exception { when(analyticsService.getSlaAnalytics()).thenReturn(null); mockMvc.perform(get("/api/analytics/sla")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void authorities_superAdmin_is200() throws Exception { when(analyticsService.getAuthorityAnalytics()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/analytics/authorities")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void resolution_superAdmin_is200() throws Exception { when(analyticsService.getResolutionAnalytics()).thenReturn(null); mockMvc.perform(get("/api/analytics/resolution")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void locations_superAdmin_is200() throws Exception { when(analyticsService.getLocationAnalytics()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/analytics/locations")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void statusTransitions_superAdmin_is200() throws Exception { when(analyticsService.getStatusTransitionAnalytics()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/analytics/status-transitions")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void geographic_superAdmin_is200() throws Exception { when(analyticsService.getGeographicAnalytics()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/analytics/geographic")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="SUPER_ADMIN") void lifecycle_superAdmin_is200() throws Exception { when(analyticsService.getLifecycleAnalytics()).thenReturn(null); mockMvc.perform(get("/api/analytics/lifecycle")).andExpect(status().isOk()); }

    @Test @WithMockUser(roles="CITIZEN") void citizenCannotReadAnalytics_is403() throws Exception { mockMvc.perform(get("/api/analytics/overview")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCannotReadAnalytics_is403() throws Exception { mockMvc.perform(get("/api/analytics/priority")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCannotReadAnalytics_is403() throws Exception { mockMvc.perform(get("/api/analytics/sla")).andExpect(status().isForbidden()); }
    @Test void unauthenticatedReadAnalytics_is401() throws Exception { mockMvc.perform(get("/api/analytics/overview")).andExpect(status().isUnauthorized()); }
}

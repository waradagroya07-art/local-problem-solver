package com.localproblemsolver.controller;

import com.localproblemsolver.config.SecurityConfig;
import com.localproblemsolver.service.CustomUserDetailsService;
import com.localproblemsolver.service.DuplicateDetectionService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = DuplicateDetectionController.class)
@Import(SecurityConfig.class)
class DuplicateDetectionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DuplicateDetectionService duplicateDetectionService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    // =========================================================
    // CITIZEN
    // =========================================================

    @Test
    @WithMockUser(
            roles = "CITIZEN"
    )
    void citizenCannotRequestDuplicates()
            throws Exception {

        mockMvc.perform(
                        get("/api/problems/1/duplicates")
                )
                .andExpect(status().isForbidden());
    }


    // =========================================================
    // AUTHORITY
    // =========================================================

    @Test
    @WithMockUser(
            roles = "AUTHORITY"
    )
    void authorityCannotRequestDuplicates()
            throws Exception {

        mockMvc.perform(
                        get("/api/problems/1/duplicates")
                )
                .andExpect(status().isForbidden());
    }


    // =========================================================
    // MODERATOR
    // =========================================================

    @Test
    @WithMockUser(
            roles = "MODERATOR"
    )
    void moderatorCanRequestDuplicates()
            throws Exception {

        when(
                duplicateDetectionService.findDuplicates(1L)
        ).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/problems/1/duplicates")
                )
                .andExpect(status().isOk());
    }


    // =========================================================
    // SUPER ADMIN
    // =========================================================

    @Test
    @WithMockUser(
            roles = "SUPER_ADMIN"
    )
    void superAdminCanRequestDuplicates()
            throws Exception {

        when(
                duplicateDetectionService.findDuplicates(1L)
        ).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/problems/1/duplicates")
                )
                .andExpect(status().isOk());
    }


    // =========================================================
    // UNAUTHENTICATED
    // =========================================================

    @Test
    void unauthenticatedRequestCannotRequestDuplicates()
            throws Exception {

        mockMvc.perform(
                        get("/api/problems/1/duplicates")
                )
                .andExpect(status().isUnauthorized());
    }
}
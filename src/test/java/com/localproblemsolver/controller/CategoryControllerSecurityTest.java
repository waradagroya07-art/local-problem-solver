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

import com.localproblemsolver.entity.Category;
import com.localproblemsolver.service.CategoryService;

@WebMvcTest(controllers = CategoryController.class)
@Import(SecurityConfig.class)
class CategoryControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean CategoryService categoryService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    @Test @WithMockUser(roles="SUPER_ADMIN") void superAdminCanCreateCategory_is200() throws Exception { when(categoryService.saveCategory(any(Category.class))).thenReturn(new Category()); mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Roads\"}")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="CITIZEN") void citizenCannotCreateCategory_is403() throws Exception { mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Roads\"}")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="CITIZEN") void authenticatedCitizenCanListCategories_is200() throws Exception { when(categoryService.findAllCategories()).thenReturn(java.util.List.of()); mockMvc.perform(get("/api/categories")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authenticatedAuthorityCanGetCategory_is200() throws Exception { when(categoryService.findCategoryById(1L)).thenReturn(new Category()); mockMvc.perform(get("/api/categories/1")).andExpect(status().isOk()); }
    @Test void unauthenticatedListCategories_is401() throws Exception { mockMvc.perform(get("/api/categories")).andExpect(status().isUnauthorized()); }
}

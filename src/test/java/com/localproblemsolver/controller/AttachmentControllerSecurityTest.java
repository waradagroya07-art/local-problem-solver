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

import com.localproblemsolver.dto.AttachmentResponse;
import com.localproblemsolver.service.AttachmentService;
import org.springframework.mock.web.MockMultipartFile;
import java.util.List;

@WebMvcTest(controllers = AttachmentController.class)
@Import(SecurityConfig.class)
class AttachmentControllerSecurityTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean AttachmentService attachmentService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;
    @Test @WithMockUser(roles="CITIZEN") void authenticatedUploadReachesController_is200() throws Exception { when(attachmentService.uploadAttachment(eq(1L), any(), anyString())).thenReturn(new AttachmentResponse()); MockMultipartFile file=new MockMultipartFile("file","evidence.jpg","image/jpeg","data".getBytes()); mockMvc.perform(multipart("/api/attachments/problems/1").file(file)).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="AUTHORITY") void authorityCanGetAttachments_is200() throws Exception { when(attachmentService.getAttachments(1L,"user")).thenReturn(List.of()); mockMvc.perform(get("/api/attachments/problems/1")).andExpect(status().isOk()); }
    @Test @WithMockUser(roles="MODERATOR") void moderatorCanDownload_is200() throws Exception { org.springframework.core.io.Resource resource=mock(org.springframework.core.io.Resource.class); when(resource.getFilename()).thenReturn("evidence.jpg"); when(resource.getURL()).thenThrow(new java.io.IOException("test")); when(attachmentService.downloadAttachment(1L,"user")).thenReturn(resource); mockMvc.perform(get("/api/attachments/1")).andExpect(status().isOk()); }
    @Test void unauthenticatedUpload_is401() throws Exception { MockMultipartFile file=new MockMultipartFile("file","evidence.jpg","image/jpeg","data".getBytes()); mockMvc.perform(multipart("/api/attachments/problems/1").file(file)).andExpect(status().isUnauthorized()); }
    @Test void unauthenticatedGet_is401() throws Exception { mockMvc.perform(get("/api/attachments/problems/1")).andExpect(status().isUnauthorized()); }
    @Test void unauthenticatedDownload_is401() throws Exception { mockMvc.perform(get("/api/attachments/1")).andExpect(status().isUnauthorized()); }
}

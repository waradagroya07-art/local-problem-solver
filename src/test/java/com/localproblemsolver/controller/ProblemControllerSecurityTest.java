package com.localproblemsolver.controller;

import com.localproblemsolver.config.SecurityConfig;
import com.localproblemsolver.dto.ProblemResponse;
import com.localproblemsolver.entity.Problem;
import com.localproblemsolver.entity.ProblemStatus;
import com.localproblemsolver.entity.Priority;
import com.localproblemsolver.entity.Severity;
import com.localproblemsolver.service.CustomUserDetailsService;
import com.localproblemsolver.service.ProblemService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProblemController.class)
@Import(SecurityConfig.class)
class ProblemControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProblemService problemService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    // =========================================================
    // GET ALL PROBLEMS
    // =========================================================

    @Test
    @WithMockUser(
            username = "citizen@gmail.com",
            roles = "CITIZEN"
    )
    void authenticatedCitizenCanGetAllProblems() throws Exception {

        when(problemService.findAllProblems())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/problems")
                )
                .andExpect(status().isOk());
    }


    @Test
    void unauthenticatedUserCannotGetAllProblems()
            throws Exception {

        mockMvc.perform(
                        get("/api/problems")
                )
                .andExpect(status().isUnauthorized());
    }


    // =========================================================
    // GET PROBLEM BY ID
    // =========================================================

    @Test
    @WithMockUser(
            username = "citizen@gmail.com",
            roles = "CITIZEN"
    )
    void authenticatedUserCanGetProblemById()
            throws Exception {

        ProblemResponse response = createResponse();

        when(problemService.findProblemById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/problems/1")
                )
                .andExpect(status().isOk());
    }


    @Test
    void unauthenticatedUserCannotGetProblemById()
            throws Exception {

        mockMvc.perform(
                        get("/api/problems/1")
                )
                .andExpect(status().isUnauthorized());
    }


    // =========================================================
    // CREATE PROBLEM
    // =========================================================

    @Test
    @WithMockUser(
            username = "citizen@gmail.com",
            roles = "CITIZEN"
    )
    void citizenCanCreateProblem()
            throws Exception {

        Problem savedProblem = new Problem();

        ProblemResponse response = createResponse();

        when(problemService.saveProblem(
                any(Problem.class),
                eq(1L),
                eq("citizen@gmail.com")
        )).thenReturn(savedProblem);

        when(problemService.convertToResponse(savedProblem))
                .thenReturn(response);

        String requestBody = """
                {
                    "title": "Broken Street Light",
                    "description": "Street light is not working",
                    "severity": "HIGH",
                    "categoryId": 1,
                    "location": "Pune",
                    "latitude": 18.5204,
                    "longitude": 73.8567
                }
                """;

        mockMvc.perform(
                        post("/api/problems")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(
            username = "authority@gmail.com",
            roles = "AUTHORITY"
    )
    void authorityCannotCreateProblem()
            throws Exception {

        String requestBody = """
                {
                    "title": "Broken Street Light",
                    "description": "Street light is not working",
                    "severity": "HIGH",
                    "categoryId": 1,
                    "location": "Pune",
                    "latitude": 18.5204,
                    "longitude": 73.8567
                }
                """;

        mockMvc.perform(
                        post("/api/problems")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(
            username = "moderator@gmail.com",
            roles = "MODERATOR"
    )
    void moderatorCannotCreateProblem()
            throws Exception {

        String requestBody = """
                {
                    "title": "Broken Street Light",
                    "description": "Street light is not working",
                    "severity": "HIGH",
                    "categoryId": 1,
                    "location": "Pune"
                }
                """;

        mockMvc.perform(
                        post("/api/problems")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void unauthenticatedUserCannotCreateProblem()
            throws Exception {

        String requestBody = """
                {
                    "title": "Broken Street Light",
                    "description": "Street light is not working",
                    "severity": "HIGH",
                    "categoryId": 1,
                    "location": "Pune"
                }
                """;

        mockMvc.perform(
                        post("/api/problems")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnauthorized());
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    @Test
    @WithMockUser(
            username = "citizen@gmail.com",
            roles = "CITIZEN"
    )
    void citizenCannotCreateProblemWithMissingRequiredFields()
            throws Exception {

        String requestBody = """
                {
                    "severity": "HIGH",
                    "categoryId": 1,
                    "location": "Pune"
                }
                """;

        mockMvc.perform(
                        post("/api/problems")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(
            username = "citizen@gmail.com",
            roles = "CITIZEN"
    )
    void citizenCannotCreateProblemWithBlankTitle()
            throws Exception {

        String requestBody = """
                {
                    "title": "",
                    "description": "Street light is broken",
                    "severity": "HIGH",
                    "categoryId": 1,
                    "location": "Pune"
                }
                """;

        mockMvc.perform(
                        post("/api/problems")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }


    // =========================================================
    // CHANGE STATUS
    // =========================================================

    @Test
    @WithMockUser(
            username = "moderator@gmail.com",
            roles = "MODERATOR"
    )
    void moderatorCanChangeProblemStatus()
            throws Exception {

        Problem problem = new Problem();
        ProblemResponse response = createResponse();

        when(problemService.changeStatus(
                eq(1L),
                eq(ProblemStatus.VALIDATED),
                eq("moderator@gmail.com")
        )).thenReturn(problem);

        when(problemService.convertToResponse(problem))
                .thenReturn(response);

        String requestBody = """
                {
                    "status": "VALIDATED"
                }
                """;

        mockMvc.perform(
                        patch("/api/problems/1/status")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(
            username = "authority@gmail.com",
            roles = "AUTHORITY"
    )
    void authorityCanAccessChangeStatusEndpoint()
            throws Exception {

        Problem problem = new Problem();
        ProblemResponse response = createResponse();

        when(problemService.changeStatus(
                eq(1L),
                eq(ProblemStatus.IN_PROGRESS),
                eq("authority@gmail.com")
        )).thenReturn(problem);

        when(problemService.convertToResponse(problem))
                .thenReturn(response);

        String requestBody = """
                {
                    "status": "IN_PROGRESS"
                }
                """;

        mockMvc.perform(
                        patch("/api/problems/1/status")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(
            username = "admin@gmail.com",
            roles = "SUPER_ADMIN"
    )
    void superAdminCanChangeProblemStatus()
            throws Exception {

        Problem problem = new Problem();
        ProblemResponse response = createResponse();

        when(problemService.changeStatus(
                eq(1L),
                eq(ProblemStatus.VALIDATED),
                eq("admin@gmail.com")
        )).thenReturn(problem);

        when(problemService.convertToResponse(problem))
                .thenReturn(response);

        String requestBody = """
                {
                    "status": "VALIDATED"
                }
                """;

        mockMvc.perform(
                        patch("/api/problems/1/status")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(
            username = "citizen@gmail.com",
            roles = "CITIZEN"
    )
    void citizenCannotChangeProblemStatus()
            throws Exception {

        String requestBody = """
                {
                    "status": "VALIDATED"
                }
                """;

        mockMvc.perform(
                        patch("/api/problems/1/status")
                                .contentType(APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }


    // =========================================================
    // CONFIRM
    // =========================================================

    @Test
    @WithMockUser(
            username = "citizen@gmail.com",
            roles = "CITIZEN"
    )
    void citizenCanAccessConfirmEndpoint()
            throws Exception {

        Problem problem = new Problem();
        ProblemResponse response = createResponse();

        when(problemService.confirmProblem(
                eq(1L),
                eq("citizen@gmail.com")
        )).thenReturn(problem);

        when(problemService.convertToResponse(problem))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/problems/1/confirm")
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(
            username = "authority@gmail.com",
            roles = "AUTHORITY"
    )
    void authorityCannotConfirmProblem()
            throws Exception {

        mockMvc.perform(
                        post("/api/problems/1/confirm")
                )
                .andExpect(status().isForbidden());
    }


    // =========================================================
    // REOPEN
    // =========================================================

    @Test
    @WithMockUser(
            username = "citizen@gmail.com",
            roles = "CITIZEN"
    )
    void citizenCanAccessReopenEndpoint()
            throws Exception {

        Problem problem = new Problem();
        ProblemResponse response = createResponse();

        when(problemService.reopenProblem(
                eq(1L),
                eq("citizen@gmail.com")
        )).thenReturn(problem);

        when(problemService.convertToResponse(problem))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/problems/1/reopen")
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(
            username = "moderator@gmail.com",
            roles = "MODERATOR"
    )
    void moderatorCannotReopenProblem()
            throws Exception {

        mockMvc.perform(
                        post("/api/problems/1/reopen")
                )
                .andExpect(status().isForbidden());
    }


    // =========================================================
    // HELPER
    // =========================================================

    private ProblemResponse createResponse() {

        return new ProblemResponse(
                1L,
                "Broken Street Light",
                "Street light is not working",
                Severity.HIGH,
                ProblemStatus.OPEN,
                Priority.HIGH,
                null,
                "Pune",
                18.5204,
                73.8567,
                null,
                null,
                null
        );
    }
}
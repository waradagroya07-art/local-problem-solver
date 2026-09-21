package com.localproblemsolver.controller;

import com.localproblemsolver.dto.analytics.AnalyticsOverviewResponse;
import com.localproblemsolver.dto.analytics.AuthorityAnalyticsResponse;
import com.localproblemsolver.dto.analytics.CategoryAnalyticsResponse;
import com.localproblemsolver.dto.analytics.GeographicAnalyticsResponse;
import com.localproblemsolver.dto.analytics.LifecycleAnalyticsResponse;
import com.localproblemsolver.dto.analytics.LocationAnalyticsResponse;
import com.localproblemsolver.dto.analytics.PriorityAnalyticsResponse;
import com.localproblemsolver.dto.analytics.ResolutionAnalyticsResponse;
import com.localproblemsolver.dto.analytics.SeverityAnalyticsResponse;
import com.localproblemsolver.dto.analytics.SlaAnalyticsResponse;
import com.localproblemsolver.dto.analytics.StatusTransitionAnalyticsResponse;
import com.localproblemsolver.dto.analytics.TrendAnalyticsResponse;
import com.localproblemsolver.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(
            AnalyticsService analyticsService) {

        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public AnalyticsOverviewResponse getOverview() {
        return analyticsService.getOverview();
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<CategoryAnalyticsResponse> getCategoryAnalytics() {
        return analyticsService.getCategoryAnalytics();
    }

    @GetMapping("/severity")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<SeverityAnalyticsResponse> getSeverityAnalytics() {
        return analyticsService.getSeverityAnalytics();
    }

    @GetMapping("/priority")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<PriorityAnalyticsResponse> getPriorityAnalytics() {
        return analyticsService.getPriorityAnalytics();
    }

    @GetMapping("/trends")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<TrendAnalyticsResponse> getTrendAnalytics(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return analyticsService.getTrendAnalytics(
                from,
                to
        );
    }

    @GetMapping("/sla")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public SlaAnalyticsResponse getSlaAnalytics() {
        return analyticsService.getSlaAnalytics();
    }

    @GetMapping("/authorities")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<AuthorityAnalyticsResponse> getAuthorityAnalytics() {
        return analyticsService.getAuthorityAnalytics();
    }

    @GetMapping("/resolution")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResolutionAnalyticsResponse getResolutionAnalytics() {
        return analyticsService.getResolutionAnalytics();
    }

    @GetMapping("/locations")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<LocationAnalyticsResponse> getLocationAnalytics() {
        return analyticsService.getLocationAnalytics();
    }

    @GetMapping("/status-transitions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<StatusTransitionAnalyticsResponse>
    getStatusTransitionAnalytics() {

        return analyticsService
                .getStatusTransitionAnalytics();
    }

    @GetMapping("/geographic")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<GeographicAnalyticsResponse>
    getGeographicAnalytics() {

        return analyticsService
                .getGeographicAnalytics();
    }

    @GetMapping("/lifecycle")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public LifecycleAnalyticsResponse
    getLifecycleAnalytics() {

        return analyticsService
                .getLifecycleAnalytics();
    }
}
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
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalyticsControllerTest {

    private final AnalyticsService service = mock(AnalyticsService.class);
    private final AnalyticsController controller =
            new AnalyticsController(service);

    @Test
    void getOverview_delegatesToService() {
        AnalyticsOverviewResponse expected =
                mock(AnalyticsOverviewResponse.class);
        when(service.getOverview()).thenReturn(expected);

        assertSame(expected, controller.getOverview());
        verify(service).getOverview();
    }

    @Test
    void getCategoryAnalytics_delegatesToService() {
        List<CategoryAnalyticsResponse> expected =
                List.of(mock(CategoryAnalyticsResponse.class));
        when(service.getCategoryAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getCategoryAnalytics());
        verify(service).getCategoryAnalytics();
    }

    @Test
    void getSeverityAnalytics_delegatesToService() {
        List<SeverityAnalyticsResponse> expected =
                List.of(mock(SeverityAnalyticsResponse.class));
        when(service.getSeverityAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getSeverityAnalytics());
        verify(service).getSeverityAnalytics();
    }

    @Test
    void getPriorityAnalytics_delegatesToService() {
        List<PriorityAnalyticsResponse> expected =
                List.of(mock(PriorityAnalyticsResponse.class));
        when(service.getPriorityAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getPriorityAnalytics());
        verify(service).getPriorityAnalytics();
    }

    @Test
    void getTrendAnalytics_passesDateRange() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);

        List<TrendAnalyticsResponse> expected =
                List.of(mock(TrendAnalyticsResponse.class));

        when(service.getTrendAnalytics(from, to)).thenReturn(expected);

        assertSame(expected, controller.getTrendAnalytics(from, to));
        verify(service).getTrendAnalytics(from, to);
    }

    @Test
    void getSlaAnalytics_delegatesToService() {
        SlaAnalyticsResponse expected =
                mock(SlaAnalyticsResponse.class);
        when(service.getSlaAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getSlaAnalytics());
        verify(service).getSlaAnalytics();
    }

    @Test
    void getAuthorityAnalytics_delegatesToService() {
        List<AuthorityAnalyticsResponse> expected =
                List.of(mock(AuthorityAnalyticsResponse.class));
        when(service.getAuthorityAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getAuthorityAnalytics());
        verify(service).getAuthorityAnalytics();
    }

    @Test
    void getResolutionAnalytics_delegatesToService() {
        ResolutionAnalyticsResponse expected =
                mock(ResolutionAnalyticsResponse.class);
        when(service.getResolutionAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getResolutionAnalytics());
        verify(service).getResolutionAnalytics();
    }

    @Test
    void getLocationAnalytics_delegatesToService() {
        List<LocationAnalyticsResponse> expected =
                List.of(mock(LocationAnalyticsResponse.class));
        when(service.getLocationAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getLocationAnalytics());
        verify(service).getLocationAnalytics();
    }

    @Test
    void getStatusTransitionAnalytics_delegatesToService() {
        List<StatusTransitionAnalyticsResponse> expected =
                List.of(mock(StatusTransitionAnalyticsResponse.class));
        when(service.getStatusTransitionAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getStatusTransitionAnalytics());
        verify(service).getStatusTransitionAnalytics();
    }

    @Test
    void getGeographicAnalytics_delegatesToService() {
        List<GeographicAnalyticsResponse> expected =
                List.of(mock(GeographicAnalyticsResponse.class));
        when(service.getGeographicAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getGeographicAnalytics());
        verify(service).getGeographicAnalytics();
    }

    @Test
    void getLifecycleAnalytics_delegatesToService() {
        LifecycleAnalyticsResponse expected =
                mock(LifecycleAnalyticsResponse.class);
        when(service.getLifecycleAnalytics()).thenReturn(expected);

        assertSame(expected, controller.getLifecycleAnalytics());
        verify(service).getLifecycleAnalytics();
    }
}

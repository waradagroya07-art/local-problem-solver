package com.localproblemsolver.dto.analytics;

public class LocationAnalyticsResponse {

    private String location;
    private long problemCount;

    public LocationAnalyticsResponse(
            String location,
            long problemCount) {

        this.location = location;
        this.problemCount = problemCount;
    }

    public String getLocation() {
        return location;
    }

    public long getProblemCount() {
        return problemCount;
    }
}
package com.localproblemsolver.dto.analytics;

public class GeographicAnalyticsResponse {

    private Long problemId;
    private String location;
    private Double latitude;
    private Double longitude;
    private String category;
    private String severity;
    private String priority;
    private String status;

    public GeographicAnalyticsResponse(
            Long problemId,
            String location,
            Double latitude,
            Double longitude,
            String category,
            String severity,
            String priority,
            String status) {

        this.problemId = problemId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.severity = severity;
        this.priority = priority;
        this.status = status;
    }

    public Long getProblemId() {
        return problemId;
    }

    public String getLocation() {
        return location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getCategory() {
        return category;
    }

    public String getSeverity() {
        return severity;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }
}
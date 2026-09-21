package com.localproblemsolver.dto.analytics;

public class StatusTransitionAnalyticsResponse {

    private String fromStatus;
    private String toStatus;
    private long transitionCount;

    public StatusTransitionAnalyticsResponse(
            String fromStatus,
            String toStatus,
            long transitionCount) {

        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.transitionCount = transitionCount;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public long getTransitionCount() {
        return transitionCount;
    }
}
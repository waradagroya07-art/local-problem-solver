package com.localproblemsolver.dto.analytics;

public class ResolutionAnalyticsResponse {

    private long totalProblems;
    private long resolvedProblems;
    private long closedProblems;
    private long reopenedProblems;
    private double resolutionRate;

    public ResolutionAnalyticsResponse(
            long totalProblems,
            long resolvedProblems,
            long closedProblems,
            long reopenedProblems,
            double resolutionRate) {

        this.totalProblems = totalProblems;
        this.resolvedProblems = resolvedProblems;
        this.closedProblems = closedProblems;
        this.reopenedProblems = reopenedProblems;
        this.resolutionRate = resolutionRate;
    }

    public long getTotalProblems() {
        return totalProblems;
    }

    public long getResolvedProblems() {
        return resolvedProblems;
    }

    public long getClosedProblems() {
        return closedProblems;
    }

    public long getReopenedProblems() {
        return reopenedProblems;
    }

    public double getResolutionRate() {
        return resolutionRate;
    }
}
package com.localproblemsolver.dto.analytics;

public class AnalyticsOverviewResponse {

    private long totalProblems;
    private long activeProblems;
    private long resolvedProblems;
    private long closedProblems;
    private long rejectedProblems;
    private long duplicateProblems;
    private long reopenedProblems;

    public AnalyticsOverviewResponse() {
    }

    public AnalyticsOverviewResponse(
            long totalProblems,
            long activeProblems,
            long resolvedProblems,
            long closedProblems,
            long rejectedProblems,
            long duplicateProblems,
            long reopenedProblems) {

        this.totalProblems = totalProblems;
        this.activeProblems = activeProblems;
        this.resolvedProblems = resolvedProblems;
        this.closedProblems = closedProblems;
        this.rejectedProblems = rejectedProblems;
        this.duplicateProblems = duplicateProblems;
        this.reopenedProblems = reopenedProblems;
    }

    public long getTotalProblems() {
        return totalProblems;
    }

    public void setTotalProblems(long totalProblems) {
        this.totalProblems = totalProblems;
    }

    public long getActiveProblems() {
        return activeProblems;
    }

    public void setActiveProblems(long activeProblems) {
        this.activeProblems = activeProblems;
    }

    public long getResolvedProblems() {
        return resolvedProblems;
    }

    public void setResolvedProblems(long resolvedProblems) {
        this.resolvedProblems = resolvedProblems;
    }

    public long getClosedProblems() {
        return closedProblems;
    }

    public void setClosedProblems(long closedProblems) {
        this.closedProblems = closedProblems;
    }

    public long getRejectedProblems() {
        return rejectedProblems;
    }

    public void setRejectedProblems(long rejectedProblems) {
        this.rejectedProblems = rejectedProblems;
    }

    public long getDuplicateProblems() {
        return duplicateProblems;
    }

    public void setDuplicateProblems(long duplicateProblems) {
        this.duplicateProblems = duplicateProblems;
    }

    public long getReopenedProblems() {
        return reopenedProblems;
    }

    public void setReopenedProblems(long reopenedProblems) {
        this.reopenedProblems = reopenedProblems;
    }
}
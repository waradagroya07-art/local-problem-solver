package com.localproblemsolver.dto.analytics;

public class AuthorityAnalyticsResponse {

    private Long authorityId;
    private String authorityName;
    private long assignedProblems;
    private long activeProblems;
    private long resolvedProblems;
    private long closedProblems;
    private long slaBreachedProblems;

    public AuthorityAnalyticsResponse() {
    }

    public AuthorityAnalyticsResponse(
            Long authorityId,
            String authorityName,
            long assignedProblems,
            long activeProblems,
            long resolvedProblems,
            long closedProblems,
            long slaBreachedProblems) {

        this.authorityId = authorityId;
        this.authorityName = authorityName;
        this.assignedProblems = assignedProblems;
        this.activeProblems = activeProblems;
        this.resolvedProblems = resolvedProblems;
        this.closedProblems = closedProblems;
        this.slaBreachedProblems = slaBreachedProblems;
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public long getAssignedProblems() {
        return assignedProblems;
    }

    public void setAssignedProblems(long assignedProblems) {
        this.assignedProblems = assignedProblems;
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

    public long getSlaBreachedProblems() {
        return slaBreachedProblems;
    }

    public void setSlaBreachedProblems(long slaBreachedProblems) {
        this.slaBreachedProblems = slaBreachedProblems;
    }
}
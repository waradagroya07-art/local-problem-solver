package com.localproblemsolver.dto.analytics;

public class SlaAnalyticsResponse {

    private long totalSlas;
    private long breachedSlas;
    private long withinSlas;
    private double compliancePercentage;

    public SlaAnalyticsResponse() {
    }

    public SlaAnalyticsResponse(
            long totalSlas,
            long breachedSlas,
            long withinSlas,
            double compliancePercentage) {

        this.totalSlas = totalSlas;
        this.breachedSlas = breachedSlas;
        this.withinSlas = withinSlas;
        this.compliancePercentage = compliancePercentage;
    }

    public long getTotalSlas() {
        return totalSlas;
    }

    public void setTotalSlas(long totalSlas) {
        this.totalSlas = totalSlas;
    }

    public long getBreachedSlas() {
        return breachedSlas;
    }

    public void setBreachedSlas(long breachedSlas) {
        this.breachedSlas = breachedSlas;
    }

    public long getWithinSlas() {
        return withinSlas;
    }

    public void setWithinSlas(long withinSlas) {
        this.withinSlas = withinSlas;
    }

    public double getCompliancePercentage() {
        return compliancePercentage;
    }

    public void setCompliancePercentage(double compliancePercentage) {
        this.compliancePercentage = compliancePercentage;
    }
}
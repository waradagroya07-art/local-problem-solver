package com.localproblemsolver.dto.analytics;

public class LifecycleAnalyticsResponse {

    private double averageOpenToValidatedHours;
    private double averageValidatedToAssignedHours;
    private double averageAssignedToInProgressHours;
    private double averageInProgressToResolvedHours;
    private double averageResolvedToClosedHours;

    public LifecycleAnalyticsResponse(
            double averageOpenToValidatedHours,
            double averageValidatedToAssignedHours,
            double averageAssignedToInProgressHours,
            double averageInProgressToResolvedHours,
            double averageResolvedToClosedHours) {

        this.averageOpenToValidatedHours =
                averageOpenToValidatedHours;

        this.averageValidatedToAssignedHours =
                averageValidatedToAssignedHours;

        this.averageAssignedToInProgressHours =
                averageAssignedToInProgressHours;

        this.averageInProgressToResolvedHours =
                averageInProgressToResolvedHours;

        this.averageResolvedToClosedHours =
                averageResolvedToClosedHours;
    }

    public double getAverageOpenToValidatedHours() {
        return averageOpenToValidatedHours;
    }

    public double getAverageValidatedToAssignedHours() {
        return averageValidatedToAssignedHours;
    }

    public double getAverageAssignedToInProgressHours() {
        return averageAssignedToInProgressHours;
    }

    public double getAverageInProgressToResolvedHours() {
        return averageInProgressToResolvedHours;
    }

    public double getAverageResolvedToClosedHours() {
        return averageResolvedToClosedHours;
    }
}
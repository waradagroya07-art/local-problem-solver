package com.localproblemsolver.dto.analytics;

import java.time.LocalDate;

public class TrendAnalyticsResponse {

    private LocalDate date;
    private long count;

    public TrendAnalyticsResponse() {
    }

    public TrendAnalyticsResponse(LocalDate date, long count) {
        this.date = date;
        this.count = count;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
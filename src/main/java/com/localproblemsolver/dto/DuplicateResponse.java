package com.localproblemsolver.dto;

public class DuplicateResponse {

    private Long problemId;
    private String title;
    private String location;
    private double similarityScore;
    private String reason;

    public DuplicateResponse() {
    }

    public DuplicateResponse(Long problemId,
                             String title,
                             String location,
                             double similarityScore,
                             String reason) {
        this.problemId = problemId;
        this.title = title;
        this.location = location;
        this.similarityScore = similarityScore;
        this.reason = reason;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
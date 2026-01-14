package com.hustleflow.ml.dto;

public class MlPredictResponse {
    private Integer employeeId;
    private String performanceScore;
    private String reviewDate;
    private String comments;

    public MlPredictResponse() {
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(String performanceScore) {
        this.performanceScore = performanceScore;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

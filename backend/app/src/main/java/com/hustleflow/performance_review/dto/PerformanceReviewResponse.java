package com.hustleflow.performance_review.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewResponse {
    private Long id;
    private Long employeeId;
    private String performanceScore;
    private Instant reviewDate;
    private String comments;
}

package com.hustleflow.performance_assessment.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponse {

    private Long employeeId;
    private Double performanceScore;
    private LocalDate reviewDate;
    private String comments;

}

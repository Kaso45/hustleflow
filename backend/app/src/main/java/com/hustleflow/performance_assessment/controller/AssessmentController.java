package com.hustleflow.performance_assessment.controller;

import org.springframework.web.bind.annotation.RestController;

import com.hustleflow.performance_assessment.dto.AssessmentResponse;
import com.hustleflow.performance_assessment.service.AssessmentService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<AssessmentResponse>> getPerformanceAssessment(@PathVariable Long employeeId) {
        return ResponseEntity.ok(assessmentService.getPerformanceAssessment(employeeId));
    }

}

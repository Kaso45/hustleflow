package com.hustleflow.performance_review.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hustleflow.performance_review.dto.CreatePerformanceReviewRequest;
import com.hustleflow.performance_review.dto.PerformanceReviewResponse;
import com.hustleflow.performance_review.service.PerformanceReviewService;

@RestController
@RequestMapping("/api/performance-reviews")
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    public PerformanceReviewController(PerformanceReviewService performanceReviewService) {
        this.performanceReviewService = performanceReviewService;
    }

    @PostMapping("")
    public ResponseEntity<PerformanceReviewResponse> createReview(@RequestBody CreatePerformanceReviewRequest request) {
        PerformanceReviewResponse response = performanceReviewService.createReview(request.getEmployeeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("")
    public ResponseEntity<Page<PerformanceReviewResponse>> getReviews(
            @RequestParam(required = false) Long employeeId,
            Pageable pageable) {
        return ResponseEntity.ok(performanceReviewService.getReviews(employeeId, pageable));
    }
}

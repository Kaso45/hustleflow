package com.hustleflow.performance_assessment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.exception.ResourceNotFoundException;
import com.hustleflow.performance_assessment.domain.Assessment;
import com.hustleflow.performance_assessment.dto.AssessmentResponse;
import com.hustleflow.performance_assessment.repository.AssessmentRepository;

@Service
public class AssessmentService {

    private AssessmentRepository assessmentRepository;
    private EmployeeRepository employeeRepository;

    public AssessmentService(AssessmentRepository assessmentRepository, EmployeeRepository employeeRepository) {
        this.assessmentRepository = assessmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<AssessmentResponse> getPerformanceAssessment(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        List<Assessment> assessments = assessmentRepository.findByEmployeeId(employeeId);

        return assessments.stream()
                .map(assessment -> new AssessmentResponse(employeeId, assessment.getPerformanceScore(),
                        assessment.getReviewDate(), assessment.getComments()))
                .toList();
    }

}

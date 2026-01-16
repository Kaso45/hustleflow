package com.hustleflow.performance_review.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.exception.MlServiceException;
import com.hustleflow.exception.ResourceNotFoundException;
import com.hustleflow.ml.dto.MlPredictRequest;
import com.hustleflow.ml.dto.MlPredictResponse;
import com.hustleflow.ml.service.MlClientService;
import com.hustleflow.performance_review.domain.PerformanceReview;
import com.hustleflow.performance_review.dto.PerformanceReviewResponse;
import com.hustleflow.performance_review.repository.PerformanceReviewRepository;

@Service
public class PerformanceReviewService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeRepository employeeRepository;
    private final MlClientService mlClientService;

    public PerformanceReviewService(
            PerformanceReviewRepository performanceReviewRepository,
            EmployeeRepository employeeRepository,
            MlClientService mlClientService) {
        this.performanceReviewRepository = performanceReviewRepository;
        this.employeeRepository = employeeRepository;
        this.mlClientService = mlClientService;
    }

    @Transactional
    public PerformanceReviewResponse createReview(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        MlPredictRequest mlRequest = new MlPredictRequest();
        mlRequest.setFeatures(buildFeatures(employee));

        MlPredictResponse mlResponse;
        try {
            mlResponse = mlClientService.predict(mlRequest);
        } catch (Exception ex) {
            throw new MlServiceException("ML service call failed", ex);
        }

        if (mlResponse == null || mlResponse.getPerformanceScore() == null
                || "ERROR".equalsIgnoreCase(mlResponse.getPerformanceScore())) {
            throw new MlServiceException("ML service prediction failed");
        }

        PerformanceReview review = PerformanceReview.builder()
                .employee(employee)
                .performanceScore(mlResponse.getPerformanceScore())
                .reviewDate(Instant.now())
                .comments(mlResponse.getComments())
                .build();

        // Map ML score to current employee performanceRating: 0=low, 1=medium, 2=high
        Integer rating = mapScoreToRating(mlResponse.getPerformanceScore());
        if (rating != null) {
            employee.setPerformanceRating(rating);
            employeeRepository.save(employee);
        }

        PerformanceReview saved = performanceReviewRepository.save(review);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<PerformanceReviewResponse> getReviews(Long employeeId, Pageable pageable) {
        Page<PerformanceReview> page;
        if (employeeId != null) {
            page = performanceReviewRepository.findAllByEmployeeId(employeeId, pageable);
        } else {
            page = performanceReviewRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    private PerformanceReviewResponse toResponse(PerformanceReview review) {
        return PerformanceReviewResponse.builder()
                .id(review.getId())
                .employeeId(review.getEmployee().getId())
                .performanceScore(review.getPerformanceScore())
                .reviewDate(review.getReviewDate())
                .comments(review.getComments())
                .build();
    }

    private Map<String, Object> buildFeatures(Employee employee) {
        Map<String, Object> features = new HashMap<>();

        features.put("EmployeeId", employee.getId());
        features.put("EmpDepartment",
                employee.getEmpDepartment() != null ? employee.getEmpDepartment().getDepartmentName() : null);
        features.put("Name", employee.getName());
        features.put("Gender", employee.getGender());
        features.put("Age", employee.getAge());
        features.put("EducationBackground", employee.getEducationBackground());
        features.put("OverTime", employee.getOverTime());
        features.put("NumCompaniesWorked", employee.getNumCompaniesWorked());
        features.put("EmpJobLevel", employee.getEmpJobLevel());
        features.put("EmpJobInvolvement", employee.getEmpJobInvolvement());
        features.put("EmpHourlyRate", employee.getEmpHourlyRate());
        features.put("EmpJobSatisfaction", employee.getEmpJobSatisfaction());
        features.put("EmpEnvironmentSatisfaction", employee.getEmpEnvironmentSatisfaction());
        features.put("MaritalStatus", employee.getMaritalStatus());
        features.put("EmpJobRole", employee.getEmpJobRole());
        features.put("BusinessTravelFrequency", employee.getBusinessTravelFrequency());
        features.put("DistanceFromHome", employee.getDistanceFromHome());
        features.put("EmpEducationLevel", employee.getEmpEducationLevel());
        features.put("EmpLastSalaryHikePercent", employee.getEmpLastSalaryHikePercent());
        features.put("EmpRelationshipSatisfaction", employee.getEmpRelationshipSatisfaction());
        features.put("TotalWorkExperienceInYears", employee.getTotalWorkExperienceInYears());
        features.put("TrainingTimesLastYear", employee.getTrainingTimesLastYear());
        features.put("EmpWorkLifeBalance", employee.getEmpWorkLifeBalance());
        features.put("ExperienceYearsAtThisCompany", employee.getExperienceYearsAtThisCompany());
        features.put("ExperienceYearsInCurrentRole", employee.getExperienceYearsInCurrentRole());
        features.put("YearsSinceLastPromotion", employee.getYearsSinceLastPromotion());
        features.put("YearsWithCurrManager", employee.getYearsWithCurrManager());
        features.put("Attrition", employee.getAttrition());
        features.put("PerformanceRating", employee.getPerformanceRating());

        return features;
    }

    private Integer mapScoreToRating(String score) {
        if (score == null)
            return null;
        String s = score.trim().toUpperCase();
        switch (s) {
            case "LOW":
                return 0;
            case "MEDIUM":
                return 1;
            case "HIGH":
                return 2;
            default:
                try {
                    int n = Integer.parseInt(s);
                    if (n >= 0 && n <= 2)
                        return n;
                } catch (NumberFormatException ignored) {
                }
                return null;
        }
    }
}

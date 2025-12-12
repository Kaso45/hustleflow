package com.hustleflow.employee.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class EmployeeCreateRequest {

    @NotBlank(message = "Department name is required")
    private String empDepartment;

    private String name;

    private String gender;

    @Min(value = 18, message = "age must be at least 18")
    private Integer age;

    private String educationBackground;

    @Min(value = 0, message = "performanceScore must be >= 0")
    @Max(value = 100, message = "performanceScore must be <= 100")
    private Integer performanceScore;

    private Boolean overTime;

    @PositiveOrZero(message = "numCompaniesWorked must be >= 0")
    private Integer numCompaniesWorked;

    @Min(value = 1, message = "empJobLevel must be between 1 and 5")
    @Max(value = 5, message = "empJobLevel must be between 1 and 5")
    private Integer empJobLevel;

    @Min(value = 1, message = "empJobInvolvement must be between 1 and 5")
    @Max(value = 5, message = "empJobInvolvement must be between 1 and 5")
    private Integer empJobInvolvement;

    @DecimalMin(value = "0.0", inclusive = true, message = "empHourlyRate must be >= 0")
    private Double empHourlyRate;

    @Min(value = 1, message = "empJobSatisfaction must be between 1 and 5")
    @Max(value = 5, message = "empJobSatisfaction must be between 1 and 5")
    private Integer empJobSatisfaction;

    @Min(value = 1, message = "empEnvironmentSatisfaction must be between 1 and 5")
    @Max(value = 5, message = "empEnvironmentSatisfaction must be between 1 and 5")
    private Integer empEnvironmentSatisfaction;

    private String maritalStatus;
    private String empJobRole;
    private String businessTravelFrequency;

    @PositiveOrZero(message = "distanceFromHome must be >= 0")
    private Integer distanceFromHome;

    @Min(value = 1, message = "empEducationLevel must be between 1 and 5")
    @Max(value = 5, message = "empEducationLevel must be between 1 and 5")
    private Integer empEducationLevel;

    @Min(value = 0, message = "empLastSalaryHikePercent must be between 0 and 100")
    @Max(value = 100, message = "empLastSalaryHikePercent must be between 0 and 100")
    private Integer empLastSalaryHikePercent;

    @Min(value = 1, message = "empRelationshipSatisfaction must be between 1 and 5")
    @Max(value = 5, message = "empRelationshipSatisfaction must be between 1 and 5")
    private Integer empRelationshipSatisfaction;

    @PositiveOrZero(message = "totalWorkExperienceInYears must be >= 0")
    private Integer totalWorkExperienceInYears;

    @PositiveOrZero(message = "trainingTimesLastYear must be >= 0")
    private Integer trainingTimesLastYear;

    @Min(value = 1, message = "empWorkLifeBalance must be between 1 and 5")
    @Max(value = 5, message = "empWorkLifeBalance must be between 1 and 5")
    private Integer empWorkLifeBalance;

    @PositiveOrZero(message = "experienceYearsAtThisCompany must be >= 0")
    private Integer experienceYearsAtThisCompany;

    @PositiveOrZero(message = "experienceYearsInCurrentRole must be >= 0")
    private Integer experienceYearsInCurrentRole;

    @PositiveOrZero(message = "yearsSinceLastPromotion must be >= 0")
    private Integer yearsSinceLastPromotion;

    @PositiveOrZero(message = "yearsWithCurrManager must be >= 0")
    private Integer yearsWithCurrManager;

    private Boolean attrition;

    @Min(value = 1, message = "performanceRating must be between 1 and 5")
    @Max(value = 5, message = "performanceRating must be between 1 and 5")
    private Integer performanceRating;

}

package com.hustleflow.employee.dto;

import lombok.Data;

@Data
public class EmployeeResponse {

    private Long id;
    private String empDepartment;
    private String name;
    private String gender;
    private Integer age;
    private String educationBackground;
    private Integer performanceScore;
    private Boolean overTime;
    private Integer numCompaniesWorked;
    private Integer empJobLevel;
    private Integer empJobInvolvement;
    private Double empHourlyRate;
    private Integer empJobSatisfaction;
    private Integer empEnvironmentSatisfaction;
    private String maritalStatus;
    private String empJobRole;
    private String businessTravelFrequency;
    private Integer distanceFromHome;
    private Integer empEducationLevel;
    private Integer empLastSalaryHikePercent;
    private Integer empRelationshipSatisfaction;
    private Integer totalWorkExperienceInYears;
    private Integer trainingTimesLastYear;
    private Integer empWorkLifeBalance;
    private Integer experienceYearsAtThisCompany;
    private Integer experienceYearsInCurrentRole;
    private Integer yearsSinceLastPromotion;
    private Integer yearsWithCurrManager;
    private Boolean attrition;
    private Integer performanceRating;

}

package com.hustleflow.employee.dto;

import com.hustleflow.employee.enums.MaritalStatus;
import com.hustleflow.employee.enums.TravelFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateRequest {

    private Long empDepartmentId;
    private String empDepartment;
    private String name;
    private String gender;
    private Integer age;
    private String educationBackground;
    private Boolean overTime;
    private Integer numCompaniesWorked;
    private Integer empJobLevel;
    private Integer empJobInvolvement;
    private Double empHourlyRate;
    private Integer empJobSatisfaction;
    private Integer empEnvironmentSatisfaction;
    private MaritalStatus maritalStatus;
    private String empJobRole;
    private TravelFrequency businessTravelFrequency;
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

package com.hustleflow.employee.domain;

import jakarta.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

import com.hustleflow.department.domain.Department;

@Entity
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_department", nullable = false)
    private String empDepartment;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "education_background")
    private String educationBackground;

    @Column(name = "performance_score")
    private Integer performanceScore;

    @Column(name = "overtime")
    private Boolean overTime;

    @Column(name = "num_companies_worked")
    private Integer numCompaniesWorked;

    @Column(name = "emp_job_level")
    private Integer empJobLevel;

    @Column(name = "emp_job_involvement")
    private Integer empJobInvolvement;

    @Column(name = "emp_hourly_rate")
    private Double empHourlyRate;

    @Column(name = "emp_job_satisfaction")
    private Integer empJobSatisfaction;

    @Column(name = "emp_environment_satisfaction")
    private Integer empEnvironmentSatisfaction;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "emp_job_role")
    private String empJobRole;

    @Column(name = "business_travel_frequency")
    private String businessTravelFrequency;

    @Column(name = "distance_from_home")
    private Integer distanceFromHome;

    @Column(name = "emp_education_level")
    private Integer empEducationLevel;

    @Column(name = "emp_last_salary_hike_percent")
    private Integer empLastSalaryHikePercent;

    @Column(name = "emp_relationship_satisfaction")
    private Integer empRelationshipSatisfaction;

    @Column(name = "total_work_experience_in_years")
    private Integer totalWorkExperienceInYears;

    @Column(name = "training_times_last_year")
    private Integer trainingTimesLastYear;

    @Column(name = "emp_work_life_balance")
    private Integer empWorkLifeBalance;

    @Column(name = "experience_years_at_this_company")
    private Integer experienceYearsAtThisCompany;

    @Column(name = "experience_years_in_current_role")
    private Integer experienceYearsInCurrentRole;

    @Column(name = "years_since_last_promotion")
    private Integer yearsSinceLastPromotion;

    @Column(name = "years_with_curr_manager")
    private Integer yearsWithCurrManager;

    @Column(name = "attrition")
    private Boolean attrition;

    @Column(name = "performance_rating")
    private Integer performanceRating;

}

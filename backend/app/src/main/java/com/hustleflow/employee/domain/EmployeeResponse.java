package com.hustleflow.employee.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeResponse {

    @JsonProperty("Employee_ID")
    private Long id;

    @JsonProperty("Department")
    private String departmentName;
    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("Age")
    private Integer age;

    @JsonProperty("Job_Title")
    private String jobTitle;

    @JsonProperty("Hire_Date")
    private LocalDateTime hireDate;

    @JsonProperty("Years_At_Company")
    private Integer yearsAtCompany;

    @JsonProperty("Education_Level")
    private String educationLevel;

    @JsonProperty("Performance_Score")
    private Integer performanceScore;

    @JsonProperty("Monthly_Salary")
    private Integer monthlySalary;

    @JsonProperty("Work_Hours_Per_Week")
    private Integer workHoursPerWeek;

    @JsonProperty("Projects_Handled")
    private Integer projectsHandled;

    @JsonProperty("Overtime_Hours")
    private Integer overtimeHours;

    @JsonProperty("Sick_Days")
    private Integer sickDays;

    @JsonProperty("Remote_Work_Frequency")
    private Integer remoteWorkFrequency;

    @JsonProperty("Team_Size")
    private Integer teamSize;

    @JsonProperty("Training_Hours")
    private Integer trainingHours;

    @JsonProperty("Promotions")
    private Integer promotions;

    @JsonProperty("Employee_Satisfaction_Score")
    private Double employeeSatisfactionScore;

    @JsonProperty("Resigned")
    private Boolean resigned;
}

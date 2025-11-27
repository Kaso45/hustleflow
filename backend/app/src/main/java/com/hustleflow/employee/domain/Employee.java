package com.hustleflow.employee.domain;

import com.hustleflow.department.domain.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
    private Department department;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    @Column(name = "years_at_company")
    private Integer yearsAtCompany;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "performance_score")
    private Integer performanceScore;

    @Column(name = "monthly_salary")
    private Integer monthlySalary;

    @Column(name = "work_hours_per_week")
    private Integer workHoursPerWeek;

    @Column(name = "projects_handled")
    private Integer projectsHandled;

    @Column(name = "overtime_hours")
    private Integer overtimeHours;

    @Column(name = "sick_days")
    private Integer sickDays;

    @Column(name = "remote_work_frequency")
    private Integer remoteWorkFrequency;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "training_hours")
    private Integer trainingHours;

    @Column(name = "promotions")
    private Integer promotions;

    @Column(name = "employee_satisfaction_score")
    private Double employeeSatisfactionScore;

    @Column(name = "resigned")
    private Boolean resigned;
}

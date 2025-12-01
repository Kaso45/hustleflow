package com.hustleflow.payroll.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeneratePayrollRequest {
    private int month;
    private int year;
    private List<String> departmentCodes; // null or empty means all departments
    private boolean applyToAllDepartments;
}


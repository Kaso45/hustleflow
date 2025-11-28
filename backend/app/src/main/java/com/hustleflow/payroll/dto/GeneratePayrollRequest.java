package com.hustleflow.payroll.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeneratePayrollRequest {
    private int month;
    private int year;
    private boolean applyAll;
    private List<String> departmentNames;
}


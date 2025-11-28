package com.hustleflow.payroll.dto;

import com.hustleflow.payroll.enums.PayrollStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePayrollRequest {
    private Long employeeId;
    private String departmentCode;
    private int month;
    private int year;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal deduction;
    private BigDecimal netSalary;
    private PayrollStatus status;
}


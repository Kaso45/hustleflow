package com.hustleflow.payroll.dto;

import com.hustleflow.payroll.enums.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollResponse {
    private Long id;
    private Long employeeId;
    private String departmentCode;
    private int month;
    private int year;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal deduction;
    private BigDecimal netSalary;
    private PayrollStatus status;
    private LocalDateTime generatedAt;
}


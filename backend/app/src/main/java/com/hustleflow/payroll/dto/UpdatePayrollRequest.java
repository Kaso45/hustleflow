package com.hustleflow.payroll.dto;

import com.hustleflow.payroll.enums.PayrollStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePayrollRequest {
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal deduction;
    private PayrollStatus status;
}

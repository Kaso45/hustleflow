package com.hustleflow.contract.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateContractRequest {
    private BigDecimal baseSalary;
    private String status; // "TERMINATED", "ACTIVE", etc.
    private LocalDate endDate;
}


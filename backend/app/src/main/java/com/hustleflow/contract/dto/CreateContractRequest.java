package com.hustleflow.contract.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hustleflow.contract.enums.ContractStatus;
import com.hustleflow.contract.enums.ContractType;

@Data
public class CreateContractRequest {
    private Long employeeId;
    private ContractType contractType;
    private LocalDate startDate;
    private LocalDate endDate; // null if indefinite
    private BigDecimal baseSalary;
    private ContractStatus status;
    private String fileUrl; // Optional
}

package com.hustleflow.contract.dto;

import com.hustleflow.contract.ContractStatus;
import com.hustleflow.contract.ContractType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

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


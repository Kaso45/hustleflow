package com.hustleflow.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hustleflow.contract.enums.ContractStatus;
import com.hustleflow.contract.enums.ContractType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractResponse {
    private Long id;
    private Long employeeId;
    private ContractType contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal baseSalary;
    private ContractStatus status;
    private String fileUrl;
}

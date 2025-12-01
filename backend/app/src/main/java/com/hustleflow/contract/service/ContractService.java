package com.hustleflow.contract.service;

import com.hustleflow.contract.domain.Contract;
import com.hustleflow.contract.dto.ContractResponse;
import com.hustleflow.contract.dto.CreateContractRequest;
import com.hustleflow.contract.dto.UpdateContractRequest;
import com.hustleflow.contract.enums.ContractStatus;
import com.hustleflow.contract.repository.ContractRepository;
import com.hustleflow.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;

    public List<ContractResponse> getAllContracts() {
        return contractRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ContractResponse createContract(CreateContractRequest request) {
        Contract contract = new Contract();
        contract.setEmployeeId(request.getEmployeeId());
        contract.setContractType(request.getContractType());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setBaseSalary(request.getBaseSalary());
        contract.setStatus(request.getStatus());
        contract.setFileUrl(request.getFileUrl());

        Contract savedContract = contractRepository.save(contract);
        return mapToResponse(savedContract);
    }

    public ContractResponse updateContract(Long contractId, UpdateContractRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));

        if (request.getBaseSalary() != null) {
            contract.setBaseSalary(request.getBaseSalary());
        }
        if (request.getStatus() != null) {
            contract.setStatus(ContractStatus.valueOf(request.getStatus()));
        }
        if (request.getEndDate() != null) {
            contract.setEndDate(request.getEndDate());
        }

        Contract updatedContract = contractRepository.save(contract);
        return mapToResponse(updatedContract);
    }

    private ContractResponse mapToResponse(Contract contract) {
        return new ContractResponse(
                contract.getId(),
                contract.getEmployeeId(),
                contract.getContractType(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getBaseSalary(),
                contract.getStatus(),
                contract.getFileUrl());
    }
}

package com.hustleflow.contract.controller;

import com.hustleflow.contract.dto.ContractResponse;
import com.hustleflow.contract.dto.CreateContractRequest;
import com.hustleflow.contract.dto.UpdateContractRequest;
import com.hustleflow.contract.service.ContractService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    // Lấy danh sách hợp đồng: GET /api/contracts
    @GetMapping
    public ResponseEntity<List<ContractResponse>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    // Tạo hợp đồng: POST /api/contracts
    @PostMapping
    public ResponseEntity<ContractResponse> createContract(@RequestBody CreateContractRequest request) {
        return ResponseEntity.ok(contractService.createContract(request));
    }

    // Cập nhật hợp đồng: PUT /api/contracts/{contractId}
    @PutMapping("/{contractId}")
    public ResponseEntity<ContractResponse> updateContract(
            @PathVariable Long contractId,
            @RequestBody UpdateContractRequest request) {
        return ResponseEntity.ok(contractService.updateContract(contractId, request));
    }
}

package com.hustleflow.payroll.controller;

import com.hustleflow.payroll.dto.CreatePayrollRequest;
import com.hustleflow.payroll.dto.GeneratePayrollRequest;
import com.hustleflow.payroll.dto.PayrollResponse;
import com.hustleflow.payroll.enums.PayrollStatus;
import com.hustleflow.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    // Lấy danh sách payroll: GET /api/payrolls
    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getPayrolls(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) PayrollStatus status) {
        return ResponseEntity.ok(payrollService.getPayrolls(month, year, status));
    }

    // Tạo payroll mới: POST /api/payrolls
    @PostMapping
    public ResponseEntity<PayrollResponse> createPayroll(@RequestBody CreatePayrollRequest request) {
        return ResponseEntity.ok(payrollService.createPayroll(request));
    }
    
    // Generate payrolls: POST /api/payrolls/generate
    @PostMapping("/generate")
    public ResponseEntity<List<PayrollResponse>> generatePayrolls(@RequestBody GeneratePayrollRequest request) {
        return ResponseEntity.ok(payrollService.generatePayrolls(request));
    }
}


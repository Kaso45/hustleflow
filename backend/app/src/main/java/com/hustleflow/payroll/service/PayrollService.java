package com.hustleflow.payroll.service;

import com.hustleflow.contract.domain.Contract;
import com.hustleflow.contract.repository.ContractRepository;
import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.payroll.domain.Payroll;
import com.hustleflow.payroll.dto.CreatePayrollRequest;
import com.hustleflow.payroll.dto.GeneratePayrollRequest;
import com.hustleflow.payroll.dto.PayrollResponse;
import com.hustleflow.payroll.enums.PayrollStatus;
import com.hustleflow.payroll.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractRepository contractRepository;

    public List<PayrollResponse> getPayrolls(Integer month, Integer year, PayrollStatus status) {
        List<Payroll> payrolls;
        
        if (month != null && year != null && status != null) {
            payrolls = payrollRepository.findByMonthAndYearAndStatus(month, year, status);
        } else if (month != null && year != null) {
            payrolls = payrollRepository.findByMonthAndYear(month, year);
        } else if (status != null) {
            payrolls = payrollRepository.findByStatus(status);
        } else {
            payrolls = payrollRepository.findAll();
        }
        
        return payrolls.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PayrollResponse createPayroll(CreatePayrollRequest request) {
        Payroll payroll = new Payroll();
        payroll.setEmployeeId(request.getEmployeeId());
        payroll.setDepartmentCode(request.getDepartmentCode());
        payroll.setMonth(request.getMonth());
        payroll.setYear(request.getYear());
        payroll.setBaseSalary(request.getBaseSalary() != null ? request.getBaseSalary() : BigDecimal.ZERO);
        payroll.setBonus(request.getBonus() != null ? request.getBonus() : BigDecimal.ZERO);
        payroll.setDeduction(request.getDeduction() != null ? request.getDeduction() : BigDecimal.ZERO);
        
        // Calculate net salary
        BigDecimal netSalary = payroll.getBaseSalary()
                .add(payroll.getBonus())
                .subtract(payroll.getDeduction());
        payroll.setNetSalary(netSalary);
        
        payroll.setStatus(request.getStatus() != null ? request.getStatus() : PayrollStatus.UNPAID);

        Payroll savedPayroll = payrollRepository.save(payroll);
        return mapToResponse(savedPayroll);
    }

    @Transactional
    public List<PayrollResponse> generatePayrolls(GeneratePayrollRequest request) {
        List<Employee> employees;
        
        if (request.isApplyToAllDepartments() || 
            request.getDepartmentCodes() == null || 
            request.getDepartmentCodes().isEmpty()) {
            employees = employeeRepository.findAll();
        } else {
            employees = employeeRepository.findByEmpDepartmentIn(request.getDepartmentCodes());
        }

        return employees.stream()
                .map(employee -> generatePayrollForEmployee(employee, request.getMonth(), request.getYear()))
                .collect(Collectors.toList());
    }

    private PayrollResponse generatePayrollForEmployee(Employee employee, int month, int year) {
        // Check if payroll already exists for this employee, month, and year
        List<Payroll> existingPayrolls = payrollRepository.findByEmployeeId(employee.getId());
        boolean exists = existingPayrolls.stream()
                .anyMatch(p -> p.getMonth() == month && p.getYear() == year);
        
        if (exists) {
            Payroll existing = existingPayrolls.stream()
                    .filter(p -> p.getMonth() == month && p.getYear() == year)
                    .findFirst()
                    .orElseThrow();
            return mapToResponse(existing);
        }

        // Get active contract for employee
        List<Contract> contracts = contractRepository.findByEmployeeId(employee.getId());
        Contract activeContract = contracts.stream()
                .filter(c -> c.getStatus() == com.hustleflow.contract.enums.ContractStatus.ACTIVE)
                .findFirst()
                .orElse(null);

        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employee.getId());
        payroll.setDepartmentCode(employee.getEmpDepartment()); // Map empDepartment to departmentCode
        payroll.setMonth(month);
        payroll.setYear(year);
        
        if (activeContract != null) {
            payroll.setBaseSalary(activeContract.getBaseSalary());
        } else {
            payroll.setBaseSalary(BigDecimal.ZERO);
        }
        
        payroll.setBonus(BigDecimal.ZERO);
        payroll.setDeduction(BigDecimal.ZERO);
        
        // Calculate net salary
        BigDecimal netSalary = payroll.getBaseSalary()
                .add(payroll.getBonus())
                .subtract(payroll.getDeduction());
        payroll.setNetSalary(netSalary);
        
        payroll.setStatus(PayrollStatus.UNPAID);

        Payroll savedPayroll = payrollRepository.save(payroll);
        return mapToResponse(savedPayroll);
    }

    private PayrollResponse mapToResponse(Payroll payroll) {
        return new PayrollResponse(
                payroll.getId(),
                payroll.getEmployeeId(),
                payroll.getDepartmentCode(),
                payroll.getMonth(),
                payroll.getYear(),
                payroll.getBaseSalary(),
                payroll.getBonus(),
                payroll.getDeduction(),
                payroll.getNetSalary(),
                payroll.getStatus(),
                payroll.getGeneratedAt());
    }
}


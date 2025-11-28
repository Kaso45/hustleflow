package com.hustleflow.payroll.service;

import com.hustleflow.contract.domain.Contract;
import com.hustleflow.contract.repository.ContractRepository;
import com.hustleflow.department.domain.Department;
import com.hustleflow.department.repository.DepartmentRepository;
import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.exception.BadRequestException;
import com.hustleflow.exception.ResourceNotFoundException;
import com.hustleflow.payroll.domain.Payroll;
import com.hustleflow.payroll.dto.CreatePayrollRequest;
import com.hustleflow.payroll.dto.GeneratePayrollRequest;
import com.hustleflow.payroll.dto.PayrollResponse;
import com.hustleflow.payroll.enums.PayrollStatus;
import com.hustleflow.payroll.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ContractRepository contractRepository;

    public List<PayrollResponse> getPayrolls(Integer month, Integer year, PayrollStatus status) {
        return payrollRepository.findAll().stream()
                .filter(payroll -> month == null || payroll.getMonth() == month)
                .filter(payroll -> year == null || payroll.getYear() == year)
                .filter(payroll -> status == null || payroll.getStatus() == status)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PayrollResponse createPayroll(CreatePayrollRequest request) {
        validateMonthYear(request.getMonth(), request.getYear());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (payrollRepository.existsByEmployeeIdAndMonthAndYear(employee.getId(), request.getMonth(), request.getYear())) {
            throw new BadRequestException("Payroll already exists for this employee in the selected period");
        }

        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employee.getId());
        payroll.setDepartmentCode(request.getDepartmentCode() != null ? request.getDepartmentCode() : employee.getDepartmentCode());
        payroll.setMonth(request.getMonth());
        payroll.setYear(request.getYear());

        BigDecimal baseSalary = safeAmount(request.getBaseSalary());
        BigDecimal bonus = safeAmount(request.getBonus());
        BigDecimal deduction = safeAmount(request.getDeduction());

        payroll.setBaseSalary(baseSalary);
        payroll.setBonus(bonus);
        payroll.setDeduction(deduction);
        payroll.setNetSalary(request.getNetSalary() != null ? request.getNetSalary() : calculateNetSalary(baseSalary, bonus, deduction));
        payroll.setStatus(request.getStatus() != null ? request.getStatus() : PayrollStatus.UNPAID);
        payroll.setGeneratedAt(LocalDateTime.now());

        return mapToResponse(payrollRepository.save(payroll));
    }

    public List<PayrollResponse> generatePayrolls(GeneratePayrollRequest request) {
        validateMonthYear(request.getMonth(), request.getYear());

        List<Employee> employees;
        if (request.isApplyAll()) {
            employees = employeeRepository.findAll();
        } else {
            if (CollectionUtils.isEmpty(request.getDepartmentNames())) {
                throw new BadRequestException("departmentNames is required when applyAll is false");
            }
            List<Department> departments = departmentRepository.findByDepartmentNameIn(request.getDepartmentNames());
            if (departments.isEmpty()) {
                throw new ResourceNotFoundException("Departments not found");
            }
            List<String> departmentCodes = departments.stream()
                    .map(Department::getCode)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (departmentCodes.isEmpty()) {
                throw new BadRequestException("Selected departments do not have valid codes");
            }
            employees = employeeRepository.findByDepartmentCodeIn(departmentCodes);
        }

        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("No employees found to generate payrolls");
        }

        List<Payroll> payrollsToSave = employees.stream()
                .filter(employee -> !payrollRepository.existsByEmployeeIdAndMonthAndYear(employee.getId(), request.getMonth(), request.getYear()))
                .map(employee -> buildGeneratedPayroll(employee, request))
                .collect(Collectors.toList());

        if (payrollsToSave.isEmpty()) {
            throw new BadRequestException("All payrolls already exist for this period");
        }

        return payrollRepository.saveAll(payrollsToSave).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Payroll buildGeneratedPayroll(Employee employee, GeneratePayrollRequest request) {
        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employee.getId());
        payroll.setDepartmentCode(employee.getDepartmentCode());
        payroll.setMonth(request.getMonth());
        payroll.setYear(request.getYear());

        BigDecimal baseSalary = resolveBaseSalary(employee.getId());
        payroll.setBaseSalary(baseSalary);
        payroll.setBonus(BigDecimal.ZERO);
        payroll.setDeduction(BigDecimal.ZERO);
        payroll.setNetSalary(baseSalary);
        payroll.setStatus(PayrollStatus.UNPAID);
        payroll.setGeneratedAt(LocalDateTime.now());
        return payroll;
    }

    private BigDecimal resolveBaseSalary(Long employeeId) {
        return contractRepository.findByEmployeeId(employeeId).stream()
                .sorted(Comparator.comparing(Contract::getStartDate).reversed())
                .map(Contract::getBaseSalary)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private void validateMonthYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must be between 1 and 12");
        }
        if (year < 1900) {
            throw new BadRequestException("Year must be greater than 1900");
        }
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
                payroll.getGeneratedAt()
        );
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal calculateNetSalary(BigDecimal base, BigDecimal bonus, BigDecimal deduction) {
        return base.add(bonus).subtract(deduction);
    }
}


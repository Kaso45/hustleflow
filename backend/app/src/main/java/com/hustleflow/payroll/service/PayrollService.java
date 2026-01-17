package com.hustleflow.payroll.service;

import com.hustleflow.contract.domain.Contract;
import com.hustleflow.contract.repository.ContractRepository;
import com.hustleflow.department.domain.Department;
import com.hustleflow.department.repository.DepartmentRepository;
import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.payroll.domain.Payroll;
import com.hustleflow.payroll.dto.CreatePayrollRequest;
import com.hustleflow.payroll.dto.GeneratePayrollRequest;
import com.hustleflow.payroll.dto.PayrollResponse;
import com.hustleflow.payroll.dto.UpdatePayrollRequest;
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
    private final DepartmentRepository departmentRepository;

    public List<PayrollResponse> getPayrolls(Integer month, Integer year, String status) {
        PayrollStatus statusEnum = null;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            try {
                statusEnum = PayrollStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid payroll status: " + status);
            }
        }
        boolean hasMonthYear = month != null && year != null;
        List<Payroll> payrolls;
        if (hasMonthYear && statusEnum != null) {
            payrolls = payrollRepository.findByMonthAndYearAndStatus(month, year, statusEnum);
        } else if (hasMonthYear) {
            payrolls = payrollRepository.findByMonthAndYear(month, year);
        } else if (statusEnum != null) {
            payrolls = payrollRepository.findByStatus(statusEnum);
        } else {
            payrolls = payrollRepository.findAll();
        }
        return payrolls.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PayrollResponse createPayroll(CreatePayrollRequest request) {
        Payroll payroll = new Payroll();

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found: " + request.getEmployeeId()));

        Department department = departmentRepository.findByCode(request.getDepartmentCode())
                .orElseThrow(() -> new RuntimeException("Department doesn't exist: " + request.getDepartmentCode()));

        payroll.setEmployee(employee);
        payroll.setDepartment(department);
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
            // Convert department codes to department IDs, then query employees by
            // department id
            List<Department> departments = departmentRepository.findByCodeIn(request.getDepartmentCodes());
            List<Long> departmentIds = departments.stream()
                    .map(Department::getId)
                    .collect(Collectors.toList());
            employees = employeeRepository.findByEmpDepartment_IdIn(departmentIds);
        }

        return employees.stream()
                .map(employee -> generatePayrollForEmployee(employee, request))
                .collect(Collectors.toList());
    }

    private PayrollResponse generatePayrollForEmployee(Employee employee, GeneratePayrollRequest request) {
        // Check if payroll already exists for this employee, month, and year
        List<Payroll> existingPayrolls = payrollRepository.findByEmployeeId(employee.getId());
        boolean exists = existingPayrolls.stream()
                .anyMatch(p -> p.getMonth() == request.getMonth() && p.getYear() == request.getYear());

        if (exists) {
            Payroll existing = existingPayrolls.stream()
                    .filter(p -> p.getMonth() == request.getMonth() && p.getYear() == request.getYear())
                    .findFirst()
                    .orElseThrow();
            return mapToResponse(existing);
        }

        // Get active contract for employee
        List<Contract> contracts = contractRepository.findByEmployee_Id(employee.getId());

        Contract activeContract = contracts.stream()
                .filter(c -> c.getStatus() == com.hustleflow.contract.enums.ContractStatus.ACTIVE)
                .findFirst()
                .orElse(null);

        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setDepartment(employee.getEmpDepartment()); // Map empDepartment to departmentCode
        payroll.setMonth(request.getMonth());
        payroll.setYear(request.getYear());

        // Apply overrides if provided; otherwise fallback to contract or zero
        if (request.getBaseSalary() != null) {
            payroll.setBaseSalary(request.getBaseSalary());
        } else if (activeContract != null) {
            payroll.setBaseSalary(activeContract.getBaseSalary());
        } else {
            payroll.setBaseSalary(BigDecimal.ZERO);
        }

        payroll.setBonus(request.getBonus() != null ? request.getBonus() : BigDecimal.ZERO);
        payroll.setDeduction(request.getDeduction() != null ? request.getDeduction() : BigDecimal.ZERO);

        // Calculate net salary
        BigDecimal netSalary = payroll.getBaseSalary()
                .add(payroll.getBonus())
                .subtract(payroll.getDeduction());
        payroll.setNetSalary(netSalary);

        payroll.setStatus(PayrollStatus.UNPAID);

        Payroll savedPayroll = payrollRepository.save(payroll);
        return mapToResponse(savedPayroll);
    }

    public PayrollResponse updatePayroll(Long id, UpdatePayrollRequest request) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found: " + id));

        if (request.getBaseSalary() != null)
            payroll.setBaseSalary(request.getBaseSalary());
        if (request.getBonus() != null)
            payroll.setBonus(request.getBonus());
        if (request.getDeduction() != null)
            payroll.setDeduction(request.getDeduction());
        if (request.getStatus() != null)
            payroll.setStatus(request.getStatus());

        BigDecimal netSalary = payroll.getBaseSalary()
                .add(payroll.getBonus() != null ? payroll.getBonus() : BigDecimal.ZERO)
                .subtract(payroll.getDeduction() != null ? payroll.getDeduction() : BigDecimal.ZERO);
        payroll.setNetSalary(netSalary);

        Payroll saved = payrollRepository.save(payroll);
        return mapToResponse(saved);
    }

    private PayrollResponse mapToResponse(Payroll payroll) {
        return new PayrollResponse(
                payroll.getId(),
                payroll.getEmployee().getId(),
                payroll.getDepartment().getCode(),
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

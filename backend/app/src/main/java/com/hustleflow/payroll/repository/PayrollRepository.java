package com.hustleflow.payroll.repository;

import com.hustleflow.payroll.domain.Payroll;
import com.hustleflow.payroll.enums.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByEmployeeId(Long employeeId);
    List<Payroll> findByMonthAndYear(Integer month, Integer year);
    List<Payroll> findByStatus(PayrollStatus status);
    List<Payroll> findByMonthAndYearAndStatus(Integer month, Integer year, PayrollStatus status);
    List<Payroll> findByDepartmentCode(String departmentCode);
    List<Payroll> findByDepartmentCodeAndMonthAndYear(String departmentCode, Integer month, Integer year);
}


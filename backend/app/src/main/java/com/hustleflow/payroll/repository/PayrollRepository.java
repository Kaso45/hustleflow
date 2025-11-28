package com.hustleflow.payroll.repository;

import com.hustleflow.payroll.domain.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    boolean existsByEmployeeIdAndMonthAndYear(Long employeeId, int month, int year);

}


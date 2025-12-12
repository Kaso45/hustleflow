package com.hustleflow.timesheet.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hustleflow.employee.domain.Employee;
import com.hustleflow.timesheet.domain.Timesheet;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    public List<Timesheet> findByEmployeeAndDateBetween(Employee employee, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = "employee")
    public List<Timesheet> findByEmployee(Employee employee);
}

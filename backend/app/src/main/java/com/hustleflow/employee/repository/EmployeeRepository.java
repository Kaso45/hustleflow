package com.hustleflow.employee.repository;

import com.hustleflow.employee.domain.Employee;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @EntityGraph(attributePaths = "empDepartment")
    List<Employee> findByEmpDepartment_Id(Long empDepartmentId);

    @EntityGraph(attributePaths = "empDepartment")
    List<Employee> findByEmpDepartment_IdIn(List<Long> empDepartmentIds);

    List<Employee> findByGender(String gender);

    List<Employee> findByAttrition(Boolean attrition);
}

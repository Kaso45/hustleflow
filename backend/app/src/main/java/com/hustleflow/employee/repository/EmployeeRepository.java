package com.hustleflow.employee.repository;

import com.hustleflow.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByEmpDepartment(String empDepartment);
    
    List<Employee> findByEmpDepartmentIn(List<String> empDepartments);

    List<Employee> findByGender(String gender);

    List<Employee> findByAttrition(Boolean attrition);
}

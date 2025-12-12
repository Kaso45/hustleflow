package com.hustleflow.department.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hustleflow.department.domain.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentName(String departmentName);

    Optional<Department> findByCode(String code);

    List<Department> findByDepartmentNameIn(List<String> departmentNames);
}

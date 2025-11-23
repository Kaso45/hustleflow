package com.hustleflow.department_service.service;

import java.util.List;

import com.hustleflow.department_service.domain.Department;
import com.hustleflow.department_service.dto.request.DepartmentRequestDTO;

public interface DepartmentService {

    public List<Department> getDepartments();

    public String createDepartment(DepartmentRequestDTO department);

    public String updateDepartment(Long id, DepartmentRequestDTO request);

    public String deleteDepartment(Long id);

}

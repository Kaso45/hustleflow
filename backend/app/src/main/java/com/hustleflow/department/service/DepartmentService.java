package com.hustleflow.department.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hustleflow.department.domain.Department;
import com.hustleflow.department.dto.request.DepartmentRequestDTO;
import com.hustleflow.department.dto.response.ManagerSummaryDto;
import com.hustleflow.department.dto.response.DeparmentResponseDTO;
import com.hustleflow.department.repository.DepartmentRepository;
import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.exception.ResourceNotFoundException;

@Service
public class DepartmentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private DepartmentRepository departmentRepository;
    private EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<DeparmentResponseDTO> getDepartments() {
        List<Department> departments = departmentRepository.findAll();

        return departments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public String createDepartment(DepartmentRequestDTO request) {
        Employee manager = employeeRepository.findById(request.getManagerId()).orElse(null);
        Optional<Department> existing = departmentRepository.findByDepartmentName(request.getDepartmentName());
        existing.ifPresent(obj -> {
            throw new RuntimeException("Department already exists: " + obj.getDepartmentName());
        });

        Department newDepartment = Department.builder()
                .departmentName(request.getDepartmentName())
                .code(request.getCode())
                .description(request.getDescription())
                .manager(manager)
                .build();

        departmentRepository.save(newDepartment);

        log.info("Department created: " + newDepartment.getDepartmentName());
        return "New department has been created: " + newDepartment.getDepartmentName();

    }

    public String updateDepartment(Long id, DepartmentRequestDTO request) {
        Employee manager = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager doesn't exist: " + request.getManagerId()));
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department doesn't exist: " + id));

        existing.setDepartmentName(request.getDepartmentName());
        existing.setCode(request.getCode());
        existing.setDescription(request.getDescription());
        existing.setManager(manager);

        departmentRepository.save(existing);

        log.info("Department updated: " + existing.getDepartmentName());
        return "Department has been updated";
    }

    public String deleteDepartment(Long id) {
        Optional<Department> existing = departmentRepository.findById(id);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Department doesn't exists: " + id);
        }

        if (existing.get().getManager() != null) {
            throw new RuntimeException("Cannot remove department as it is holding a reference to another object");
        }

        departmentRepository.deleteById(id);
        log.info("Department removed: " + id);
        return "Department has been deleted";
    }

    private DeparmentResponseDTO mapToResponse(Department department) {
        DeparmentResponseDTO dto = DeparmentResponseDTO.builder()
                .id(department.getId())
                .departmentName(department.getDepartmentName())
                .code(department.getCode())
                .description(department.getDescription()).build();

        if (department.getManager() != null) {
            ManagerSummaryDto managerSummary = ManagerSummaryDto.builder().id(department.getManager().getId())
                    .name(department.getManager().getName()).age(department.getManager().getAge()).build();
            dto.setManager(managerSummary);
        }

        return dto;
    }

}

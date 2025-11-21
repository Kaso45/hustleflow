package com.hustleflow.department_service.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hustleflow.department_service.domain.Department;
import com.hustleflow.department_service.dto.request.DepartmentRequestDTO;
import com.hustleflow.department_service.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public String createDepartment(DepartmentRequestDTO request) {
        Optional<Department> existing = departmentRepository.findByDepartmentName(request.getDepartmentName());
        existing.ifPresent(obj -> {
            throw new RuntimeException("Department already exists: " + obj.getDepartmentName());
        });

        Department newDepartment = Department.builder()
                .departmentName(request.getDepartmentName())
                .code(request.getCode())
                .description(request.getDescription())
                .managerId(request.getManagerId())
                .build();

        departmentRepository.save(newDepartment);

        log.info("Department created: " + newDepartment.getDepartmentName());
        return "New department has been created: " + newDepartment.getDepartmentName();

    }

    @Override
    public String updateDepartment(Long id, DepartmentRequestDTO request) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department doesn't exist: " + id));

        existing.setDepartmentName(request.getDepartmentName());
        existing.setCode(request.getCode());
        existing.setDescription(request.getDescription());
        existing.setManagerId(request.getManagerId());

        departmentRepository.save(existing);

        log.info("Department updated: " + existing.getDepartmentName());
        return "Department has been updated";
    }

    @Override
    public String deleteDepartment(Long id) {
        Optional<Department> existing = departmentRepository.findById(id);
        if (existing.isEmpty()) {
            throw new RuntimeException("Department doesn't exists: " + id);
        }

        departmentRepository.deleteById(id);
        log.info("Department removed: " + id);
        return "Department has been deleted";
    }

}

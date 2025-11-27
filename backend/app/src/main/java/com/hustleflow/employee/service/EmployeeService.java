package com.hustleflow.employee.service;

import com.hustleflow.department.domain.Department;
import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.domain.EmployeeRequest;
import com.hustleflow.employee.domain.EmployeeResponse;
import com.hustleflow.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public List<EmployeeResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse getById(Long id) {
        return repo.findById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    public Long create(EmployeeRequest req) {
        Employee e = fromRequest(req);

        Department deptRef = Department.builder()
                .id(req.getDepartmentId())
                .build();
        e.setDepartment(deptRef);

        repo.save(e);
        return e.getId();
    }

    public EmployeeResponse update(Long employeeId, EmployeeRequest req) {
        Optional<Employee> opt = repo.findById(employeeId);
        if (opt.isEmpty()) {
            return null;
        }

        Employee e = opt.get();

        Department deptRef = Department.builder()
                .id(req.getDepartmentId())
                .build();
        e.setDepartment(deptRef);

        e.setGender(req.getGender());
        e.setAge(req.getAge());
        e.setJobTitle(req.getJobTitle());
        e.setHireDate(req.getHireDate());
        e.setYearsAtCompany(req.getYearsAtCompany());
        e.setEducationLevel(req.getEducationLevel());
        e.setPerformanceScore(req.getPerformanceScore());
        e.setMonthlySalary(req.getMonthlySalary());
        e.setWorkHoursPerWeek(req.getWorkHoursPerWeek());
        e.setProjectsHandled(req.getProjectsHandled());
        e.setOvertimeHours(req.getOvertimeHours());
        e.setSickDays(req.getSickDays());
        e.setRemoteWorkFrequency(req.getRemoteWorkFrequency());
        e.setTeamSize(req.getTeamSize());
        e.setTrainingHours(req.getTrainingHours());
        e.setPromotions(req.getPromotions());
        e.setEmployeeSatisfactionScore(req.getEmployeeSatisfactionScore());
        e.setResigned(req.getResigned());

        repo.save(e);
        return toResponse(e);
    }

    public void delete(Long employeeId) {
        Optional<Employee> opt = repo.findById(employeeId);
        opt.ifPresent(repo::delete);
    }

    private EmployeeResponse toResponse(Employee e) {
        EmployeeResponse dto = new EmployeeResponse();
        dto.setId(e.getId());

        if (e.getDepartment() != null) {
            dto.setDepartmentName(e.getDepartment().getDepartmentName());
        }

        dto.setGender(e.getGender());
        dto.setAge(e.getAge());
        dto.setJobTitle(e.getJobTitle());
        dto.setHireDate(e.getHireDate());
        dto.setYearsAtCompany(e.getYearsAtCompany());
        dto.setEducationLevel(e.getEducationLevel());
        dto.setPerformanceScore(e.getPerformanceScore());
        dto.setMonthlySalary(e.getMonthlySalary());
        dto.setWorkHoursPerWeek(e.getWorkHoursPerWeek());
        dto.setProjectsHandled(e.getProjectsHandled());
        dto.setOvertimeHours(e.getOvertimeHours());
        dto.setSickDays(e.getSickDays());
        dto.setRemoteWorkFrequency(e.getRemoteWorkFrequency());
        dto.setTeamSize(e.getTeamSize());
        dto.setTrainingHours(e.getTrainingHours());
        dto.setPromotions(e.getPromotions());
        dto.setEmployeeSatisfactionScore(e.getEmployeeSatisfactionScore());
        dto.setResigned(e.getResigned());
        return dto;
    }

    private Employee fromRequest(EmployeeRequest req) {
        Employee e = new Employee();
        e.setGender(req.getGender());
        e.setAge(req.getAge());
        e.setJobTitle(req.getJobTitle());
        e.setHireDate(req.getHireDate());
        e.setYearsAtCompany(req.getYearsAtCompany());
        e.setEducationLevel(req.getEducationLevel());
        e.setPerformanceScore(req.getPerformanceScore());
        e.setMonthlySalary(req.getMonthlySalary());
        e.setWorkHoursPerWeek(req.getWorkHoursPerWeek());
        e.setProjectsHandled(req.getProjectsHandled());
        e.setOvertimeHours(req.getOvertimeHours());
        e.setSickDays(req.getSickDays());
        e.setRemoteWorkFrequency(req.getRemoteWorkFrequency());
        e.setTeamSize(req.getTeamSize());
        e.setTrainingHours(req.getTrainingHours());
        e.setPromotions(req.getPromotions());
        e.setEmployeeSatisfactionScore(req.getEmployeeSatisfactionScore());
        e.setResigned(req.getResigned());
        return e;
    }
}

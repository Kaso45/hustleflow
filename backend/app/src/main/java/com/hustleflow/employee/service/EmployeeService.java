package com.hustleflow.employee.service;

import com.hustleflow.department.domain.Department;
import com.hustleflow.department.repository.DepartmentRepository;
import com.hustleflow.employee.domain.Employee;
import com.hustleflow.employee.dto.EmployeeCreateRequest;
import com.hustleflow.employee.dto.EmployeeResponse;
import com.hustleflow.employee.dto.EmployeeUpdateRequest;
import com.hustleflow.employee.repository.EmployeeRepository;
import com.hustleflow.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        Employee employee = new Employee();

        if (request.getEmpDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getEmpDepartmentId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException(
                                    "Department doesn't exist: " + request.getEmpDepartmentId()));
            employee.setEmpDepartment(department);
        } else {
            employee.setEmpDepartment(null);
        }
        employee.setName(request.getName());
        employee.setGender(request.getGender());
        employee.setAge(request.getAge());
        employee.setEducationBackground(request.getEducationBackground());
        employee.setOverTime(request.getOverTime());
        employee.setNumCompaniesWorked(request.getNumCompaniesWorked());
        employee.setEmpJobLevel(request.getEmpJobLevel());
        employee.setEmpJobInvolvement(request.getEmpJobInvolvement());
        employee.setEmpHourlyRate(request.getEmpHourlyRate());
        employee.setEmpJobSatisfaction(request.getEmpJobSatisfaction());
        employee.setEmpEnvironmentSatisfaction(request.getEmpEnvironmentSatisfaction());
        employee.setMaritalStatus(request.getMaritalStatus());
        employee.setEmpJobRole(request.getEmpJobRole());
        employee.setBusinessTravelFrequency(request.getBusinessTravelFrequency());
        employee.setDistanceFromHome(request.getDistanceFromHome());
        employee.setEmpEducationLevel(request.getEmpEducationLevel());
        employee.setEmpLastSalaryHikePercent(request.getEmpLastSalaryHikePercent());
        employee.setEmpRelationshipSatisfaction(request.getEmpRelationshipSatisfaction());
        employee.setTotalWorkExperienceInYears(request.getTotalWorkExperienceInYears());
        employee.setTrainingTimesLastYear(request.getTrainingTimesLastYear());
        employee.setEmpWorkLifeBalance(request.getEmpWorkLifeBalance());
        employee.setExperienceYearsAtThisCompany(request.getExperienceYearsAtThisCompany());
        employee.setExperienceYearsInCurrentRole(request.getExperienceYearsInCurrentRole());
        employee.setYearsSinceLastPromotion(request.getYearsSinceLastPromotion());
        employee.setYearsWithCurrManager(request.getYearsWithCurrManager());
        employee.setAttrition(request.getAttrition());
        employee.setPerformanceRating(request.getPerformanceRating());

        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponse(savedEmployee);
    }

    public List<EmployeeResponse> getEmployees(Long departmentId) {
        List<Employee> employees;
        if (departmentId != null) {
            employees = employeeRepository.findByEmpDepartment_Id(departmentId);
        } else {
            employees = employeeRepository.findAll();
        }
        return employees.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse getEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        return mapToResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long employeeId, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (request.getEmpDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getEmpDepartmentId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException(
                                    "Department doesn't exist: " + request.getEmpDepartmentId()));
            employee.setEmpDepartment(department);
        } else {
            // allow clearing department by sending null
            employee.setEmpDepartment(null);
        }
        employee.setName(request.getName());
        employee.setGender(request.getGender());
        employee.setAge(request.getAge());
        employee.setEducationBackground(request.getEducationBackground());
        employee.setOverTime(request.getOverTime());
        employee.setNumCompaniesWorked(request.getNumCompaniesWorked());
        employee.setEmpJobLevel(request.getEmpJobLevel());
        employee.setEmpJobInvolvement(request.getEmpJobInvolvement());
        employee.setEmpHourlyRate(request.getEmpHourlyRate());
        employee.setEmpJobSatisfaction(request.getEmpJobSatisfaction());
        employee.setEmpEnvironmentSatisfaction(request.getEmpEnvironmentSatisfaction());
        employee.setMaritalStatus(request.getMaritalStatus());
        employee.setEmpJobRole(request.getEmpJobRole());
        employee.setBusinessTravelFrequency(request.getBusinessTravelFrequency());
        employee.setDistanceFromHome(request.getDistanceFromHome());
        employee.setEmpEducationLevel(request.getEmpEducationLevel());
        employee.setEmpLastSalaryHikePercent(request.getEmpLastSalaryHikePercent());
        employee.setEmpRelationshipSatisfaction(request.getEmpRelationshipSatisfaction());
        employee.setTotalWorkExperienceInYears(request.getTotalWorkExperienceInYears());
        employee.setTrainingTimesLastYear(request.getTrainingTimesLastYear());
        employee.setEmpWorkLifeBalance(request.getEmpWorkLifeBalance());
        employee.setExperienceYearsAtThisCompany(request.getExperienceYearsAtThisCompany());
        employee.setExperienceYearsInCurrentRole(request.getExperienceYearsInCurrentRole());
        employee.setYearsSinceLastPromotion(request.getYearsSinceLastPromotion());
        employee.setYearsWithCurrManager(request.getYearsWithCurrManager());
        employee.setAttrition(request.getAttrition());
        employee.setPerformanceRating(request.getPerformanceRating());

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();

        Long departmentId = employee.getEmpDepartment() != null ? employee.getEmpDepartment().getId() : null;
        String departmentName = employee.getEmpDepartment() != null ? employee.getEmpDepartment().getDepartmentName()
                : null;

        response.setId(employee.getId());
        response.setEmpDepartmentId(departmentId);
        response.setEmpDepartment(departmentName);
        response.setName(employee.getName());
        response.setGender(employee.getGender());
        response.setAge(employee.getAge());
        response.setEducationBackground(employee.getEducationBackground());
        response.setOverTime(employee.getOverTime());
        response.setNumCompaniesWorked(employee.getNumCompaniesWorked());
        response.setEmpJobLevel(employee.getEmpJobLevel());
        response.setEmpJobInvolvement(employee.getEmpJobInvolvement());
        response.setEmpHourlyRate(employee.getEmpHourlyRate());
        response.setEmpJobSatisfaction(employee.getEmpJobSatisfaction());
        response.setEmpEnvironmentSatisfaction(employee.getEmpEnvironmentSatisfaction());
        response.setMaritalStatus(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : null);
        response.setEmpJobRole(employee.getEmpJobRole());
        response.setBusinessTravelFrequency(
                employee.getBusinessTravelFrequency() != null ? employee.getBusinessTravelFrequency().name() : null);
        response.setDistanceFromHome(employee.getDistanceFromHome());
        response.setEmpEducationLevel(employee.getEmpEducationLevel());
        response.setEmpLastSalaryHikePercent(employee.getEmpLastSalaryHikePercent());
        response.setEmpRelationshipSatisfaction(employee.getEmpRelationshipSatisfaction());
        response.setTotalWorkExperienceInYears(employee.getTotalWorkExperienceInYears());
        response.setTrainingTimesLastYear(employee.getTrainingTimesLastYear());
        response.setEmpWorkLifeBalance(employee.getEmpWorkLifeBalance());
        response.setExperienceYearsAtThisCompany(employee.getExperienceYearsAtThisCompany());
        response.setExperienceYearsInCurrentRole(employee.getExperienceYearsInCurrentRole());
        response.setYearsSinceLastPromotion(employee.getYearsSinceLastPromotion());
        response.setYearsWithCurrManager(employee.getYearsWithCurrManager());
        response.setAttrition(employee.getAttrition());
        response.setPerformanceRating(employee.getPerformanceRating());
        return response;
    }
}
